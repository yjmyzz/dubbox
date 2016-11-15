package com.alibaba.dubbo.rpc.protocol.thrift2;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.protocol.AbstractProxyProtocol;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.*;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 为dubbo-rpc添加"原生thrift"支持
 * by 杨俊明(http://yjmyzz.cnblogs.com/)
 */
public class Thrift2Protocol extends AbstractProxyProtocol {
    public static final int DEFAULT_PORT = 33208;
    private static final Logger logger = LoggerFactory.getLogger(Thrift2Protocol.class);

    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    public Thrift2Protocol() {
        super(TException.class, RpcException.class);
    }


    @Override
    protected <T> Runnable doExport(T impl, Class<T> type, URL url)
            throws RpcException {

        logger.info("impl => " + impl.getClass());
        logger.info("type => " + type.getName());
        logger.info("url => " + url);

        return exportNonblockingServer(impl, type, url);
        //return exportThreadPoolServer(impl, type, url);
    }

    @Override
    protected <T> T doRefer(Class<T> type, URL url) throws RpcException {
        logger.info("type => " + type.getName());
        logger.info("url => " + url);
        return doReferFrameAndCompact(type, url);
    }


    private <T> Runnable exportNonblockingServer(T impl, Class<T> type, URL url)
            throws RpcException {
        TProcessor tprocessor;
        TNonblockingServer.Args tArgs = null;
        String iFace = "$Iface";
        String processor = "$Processor";
        String typeName = type.getName();
        TNonblockingServerSocket transport;
        if (typeName.endsWith(iFace)) {
            String processorClsName = typeName.substring(0, typeName.indexOf(iFace)) + processor;
            try {
                Class<?> clazz = Class.forName(processorClsName);
                Constructor constructor = clazz.getConstructor(type);
                try {
                    tprocessor = (TProcessor) constructor.newInstance(impl);
                    transport = new TNonblockingServerSocket(url.getPort());
                    tArgs = new TNonblockingServer.Args(transport);
                    tArgs.processor(tprocessor);
                    tArgs.transportFactory(new TFramedTransport.Factory());
                    tArgs.protocolFactory(new TCompactProtocol.Factory());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RpcException("Fail to create thrift server(" + url + ") : " + e.getMessage(), e);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RpcException("Fail to create thrift server(" + url + ") : " + e.getMessage(), e);
            }
        }

        if (tArgs == null) {
            logger.error("Fail to create thrift server(" + url + ") due to null args");
            throw new RpcException("Fail to create thrift server(" + url + ") due to null args");
        }
        final TServer thriftServer = new TNonblockingServer(tArgs);

        new Thread(new Runnable() {
            public void run() {
                logger.info("Start Thrift NonblockingServer");
                thriftServer.serve();
                logger.info("Thrift NonblockingServer started.");
            }
        }).start();

        return new Runnable() {
            public void run() {
                try {
                    logger.info("Close Thrift NonblockingServer");
                    thriftServer.stop();
                } catch (Throwable e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        };
    }

    private <T> Runnable exportThreadPoolServer(T impl, Class<T> type, URL url)
            throws RpcException {
        TProcessor tprocessor;
        TThreadPoolServer.Args tArgs = null;
        String iFace = "$Iface";
        String processor = "$Processor";
        String typeName = type.getName();
        TServerTransport transport;
        if (typeName.endsWith(iFace)) {
            String processorClsName = typeName.substring(0, typeName.indexOf(iFace)) + processor;
            try {
                Class<?> clazz = Class.forName(processorClsName);
                Constructor constructor = clazz.getConstructor(type);
                try {
                    tprocessor = (TProcessor) constructor.newInstance(impl);
                    transport = new TServerSocket(url.getPort());
                    tArgs = new TThreadPoolServer.Args(transport);
                    tArgs.processor(tprocessor);
                    tArgs.executorService(Executors.newFixedThreadPool(100));
                    tArgs.protocolFactory(new TBinaryProtocol.Factory());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RpcException("Fail to create thrift server(" + url + ") : " + e.getMessage(), e);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RpcException("Fail to create thrift server(" + url + ") : " + e.getMessage(), e);
            }
        }

        if (tArgs == null) {
            logger.error("Fail to create thrift server(" + url + ") due to null args");
            throw new RpcException("Fail to create thrift server(" + url + ") due to null args");
        }
        final TServer thriftServer = new TThreadPoolServer(tArgs);

        ExecutorService service = Executors.newFixedThreadPool(50);
        service.submit(() -> {
            logger.info("Start Thrift ThreadPoolServer");
            thriftServer.serve();
            logger.info("Thrift ThreadPoolServer started.");
        });

        return () -> {
            try {
                logger.info("Close Thrift ThreadPoolServer");
                thriftServer.stop();
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
        };
    }


    private <T> T doReferFrameAndCompact(Class<T> type, URL url) throws RpcException {

        try {
            TSocket tSocket;
            TTransport transport;
            TProtocol protocol;
            T thriftClient = null;
            String iFace = "$Iface";
            String client = "$Client";

            String typeName = type.getName();
            if (typeName.endsWith(iFace)) {
                String clientClsName = typeName.substring(0, typeName.indexOf(iFace)) + client;
                Class<?> clazz = Class.forName(clientClsName);
                Constructor constructor = clazz.getConstructor(TProtocol.class);
                try {
                    tSocket = new TSocket(url.getHost(), url.getPort());
                    transport = new TFramedTransport(tSocket);
                    protocol = new TCompactProtocol(transport);
                    thriftClient = (T) constructor.newInstance(protocol);
                    transport.open();
                    logger.info("thrift client opened for service(" + url + ")");
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RpcException("Fail to create remote client:" + e.getMessage(), e);
                }
            }
            return thriftClient;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RpcException("Fail to create remote client for service(" + url + "): " + e.getMessage(), e);
        }
    }


    private <T> T doReferBinary(Class<T> type, URL url) throws RpcException {

        try {
            TTransport transport;
            TProtocol protocol;
            T thriftClient = null;
            String iFace = "$Iface";
            String client = "$Client";

            String typeName = type.getName();
            if (typeName.endsWith(iFace)) {
                String clientClsName = typeName.substring(0, typeName.indexOf(iFace)) + client;
                Class<?> clazz = Class.forName(clientClsName);
                Constructor constructor = clazz.getConstructor(TProtocol.class);
                try {
                    transport = new TSocket(url.getHost(), url.getPort());
                    protocol = new TBinaryProtocol(transport);
                    thriftClient = (T) constructor.newInstance(protocol);
                    transport.open();
                    logger.info("thrift client opened for service(" + url + ")");
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RpcException("Fail to create remote client:" + e.getMessage(), e);
                }
            }
            return thriftClient;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RpcException("Fail to create remote client for service(" + url + "): " + e.getMessage(), e);
        }
    }

}

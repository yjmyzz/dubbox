package com.alibaba.dubbo.rpc.protocol.grpc;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.protocol.AbstractProxyProtocol;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * 为dubbo-rpc添加"google-gRPC"支持
 * by 杨俊明(http://yjmyzz.cnblogs.com/)
 */
public class GrpcProtocol extends AbstractProxyProtocol {
    public static final int DEFAULT_PORT = 50051;
    private static final Logger logger = LoggerFactory.getLogger(GrpcProtocol.class);

    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    public GrpcProtocol() {
        super(IOException.class, RpcException.class);
    }

    @Override
    protected <T> Runnable doExport(T impl, Class<T> type, URL url)
            throws RpcException {

        logger.info("impl => " + impl.getClass());
        logger.info("type => " + type.getName());
        logger.info("url => " + url);

        try {
            String clsName = url.getParameter("class");
            Class<?> cls = Class.forName(clsName);
            GrpcBindableService service = (GrpcBindableService) cls.newInstance();
            final Server grpcServer = ServerBuilder.forPort(url.getPort())
                    .addService(service)
                    .build()
                    .start();
            logger.info("grpc server started !");
            return new Runnable() {
                public void run() {
                    try {
                        logger.info("Close gRPC Server");
                        grpcServer.shutdown();
                    } catch (Throwable e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            };
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RpcException(e.getMessage(), e);
        }
    }

    @Override
    protected <T> T doRefer(Class<T> type, URL url) throws RpcException {
        logger.info("type => " + type.getName());
        logger.info("url => " + url);
        final ManagedChannel channel = ManagedChannelBuilder.forAddress(url.getHost(), url.getPort())
                .usePlaintext(true)
                .build();
        try {
            DefaultBindableService service = new DefaultBindableService();
            service.setChannel(channel);
            return (T) service;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RpcException(e.getMessage(), e);
        }
    }

}

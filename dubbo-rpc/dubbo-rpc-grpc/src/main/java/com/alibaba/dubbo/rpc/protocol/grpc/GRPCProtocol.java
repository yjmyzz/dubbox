package com.alibaba.dubbo.rpc.protocol.grpc;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.protocol.AbstractProxyProtocol;
import io.grpc.*;

import java.io.IOException;

/**
 * 为dubbo-rpc添加"google-gRPC"支持
 * by 杨俊明(http://yjmyzz.cnblogs.com/)
 */
public class GRPCProtocol extends AbstractProxyProtocol {
    public static final int DEFAULT_PORT = 50051;
    private static final Logger logger = LoggerFactory.getLogger(GRPCProtocol.class);

    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    public GRPCProtocol() {
        super(IOException.class, RpcException.class);
    }

    @Override
    protected <T> Runnable doExport(T impl, Class<T> type, URL url)
            throws RpcException {

        logger.info("impl => " + impl.getClass());
        logger.info("type => " + type.getName());
        logger.info("url => " + url);

        try {
            BindableService service = (BindableService) type.newInstance();
            final Server grpcServer = ServerBuilder.forPort(url.getPort())
                    .addService(service)
                    .build()
                    .start();

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

        return (T) channel;
    }

}

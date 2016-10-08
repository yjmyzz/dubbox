package com.alibaba.dubbo.rpc.protocol.grpc;

import io.grpc.Channel;
import io.grpc.ServerServiceDefinition;

/**
 * Created by yangjunming on 16/10/7.
 */
public class DefaultBindableService implements GrpcBindableService {

    private Channel channel;

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public ServerServiceDefinition bindService() {
        return null;
    }
}

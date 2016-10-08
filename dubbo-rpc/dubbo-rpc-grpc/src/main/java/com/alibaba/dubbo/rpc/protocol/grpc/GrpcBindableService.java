package com.alibaba.dubbo.rpc.protocol.grpc;

import io.grpc.BindableService;
import io.grpc.Channel;

/**
 * Created by yangjunming on 16/10/7.
 */
public interface GrpcBindableService extends BindableService {

    Channel getChannel();

    void setChannel(Channel channel);
}

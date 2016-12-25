package com.cnblogs.yjmyzz.demo.service.impl.grpc;

import com.alibaba.dubbo.rpc.protocol.grpc.GrpcBindableService;
import com.cnblogs.yjmyzz.demo.service.api.grpc.GrpcHelloServiceGrpc;
import com.cnblogs.yjmyzz.demo.service.api.grpc.PingRequest;
import com.cnblogs.yjmyzz.demo.service.api.grpc.PingResponse;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

/**
 * Created by yangjunming on 2016/11/3.
 */
@Service("grpcService")
public class HelloServiceImpl extends GrpcHelloServiceGrpc.GrpcHelloServiceImplBase implements GrpcBindableService {

    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void ping(PingRequest request,
                     StreamObserver<PingResponse> responseObserver) {
        PingResponse reply = PingResponse.newBuilder().setMessage("grpc is running").build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}

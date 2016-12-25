package com.cnblogs.yjmyzz.demo.service.consumer;

import com.alibaba.dubbo.rpc.protocol.grpc.GrpcBindableService;
import com.cnblogs.yjmyzz.demo.service.api.avro.AvroHelloService;
import com.cnblogs.yjmyzz.demo.service.api.dubbo.DubboHelloService;
import com.cnblogs.yjmyzz.demo.service.api.dubbo.User;
import com.cnblogs.yjmyzz.demo.service.api.grpc.GrpcHelloServiceGrpc;
import com.cnblogs.yjmyzz.demo.service.api.grpc.PingRequest;
import com.cnblogs.yjmyzz.demo.service.api.grpc.PingResponse;
import com.cnblogs.yjmyzz.demo.service.api.rest.RestHelloService;
import com.cnblogs.yjmyzz.demo.service.api.thrift.ThriftHelloService;
import io.grpc.ManagedChannel;
import io.grpc.stub.AbstractStub;
import org.apache.avro.AvroRemoteException;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Created by yangjunming on 2016/12/25.
 */
public class DubboDemoConsumer {

    private static Logger logger = LoggerFactory.getLogger(DubboDemoConsumer.class);

    public static void main(String[] args) throws TException, InterruptedException, AvroRemoteException {

        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("spring-context.xml");

        testRest(ctx);
        testDubbo(ctx);
        testThrift(ctx);
        testAvro(ctx);
        testGrpc(ctx);
    }

    private static void testRest(ConfigurableApplicationContext ctx) {
        RestHelloService service = ctx.getBean(RestHelloService.class);
        logger.info("\n---------restful服务测试开始---------");
        logger.info("\tping=>" + service.ping());
        logger.info("\tregister=>" + service.register(mockUser()));
    }


    private static void testDubbo(ConfigurableApplicationContext ctx) {
        DubboHelloService service = ctx.getBean(DubboHelloService.class);
        logger.info("\n---------dubbo协议测试开始---------");
        logger.info("\tping=>" + service.ping());
        logger.info("\tregister=>" + service.register(mockUser()));
    }


    private static void testThrift(ConfigurableApplicationContext ctx) throws TException {
        ThriftHelloService.Iface service = ctx.getBean(ThriftHelloService.Iface.class);
        logger.info("\n---------thrift协议测试开始---------");
        logger.info("\tping=>" + service.ping());
    }

    private static void testAvro(ConfigurableApplicationContext ctx) throws AvroRemoteException {
        AvroHelloService service = ctx.getBean(AvroHelloService.class);
        logger.info("\n---------avro协议测试开始---------");
        logger.info("\tping=>" + service.ping());
    }

    private static void testGrpc(ConfigurableApplicationContext ctx) throws InterruptedException {
        GrpcBindableService service = ctx.getBean(GrpcBindableService.class, "grpcService");
        AbstractStub stub = GrpcHelloServiceGrpc.newBlockingStub(service.getChannel());
        PingRequest request = PingRequest.newBuilder().build();
        logger.info("\n---------gprc协议测试开始---------");
        logger.info(stub.getClass().toString());
        PingResponse response = ((GrpcHelloServiceGrpc.GrpcHelloServiceBlockingStub) stub).ping(request);
        logger.info("\tping=>" + response.getMessage());
        ((ManagedChannel) stub.getChannel()).shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }


    private static User mockUser() {
        User u = new User();
        u.setUserId(1);
        u.setUserName("菩提树下的杨过");
        return u;
    }
}

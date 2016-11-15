package com.cnblogs.yjmyzz.demo.service.api.dubbo;

/**
 * Created by yangjunming on 2016/11/2.
 */
public interface DubboHelloService {

    String ping();

    String register(User user);
}

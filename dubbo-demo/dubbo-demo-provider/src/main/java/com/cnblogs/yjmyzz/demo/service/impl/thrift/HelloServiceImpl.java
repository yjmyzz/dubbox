package com.cnblogs.yjmyzz.demo.service.impl.thrift;

import com.cnblogs.yjmyzz.demo.service.api.thrift.ThriftHelloService;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

/**
 * Created by yangjunming on 2016/11/3.
 */
@Service("thriftService")
public class HelloServiceImpl implements ThriftHelloService.Iface {

    @Override
    public String ping() throws TException {
        return "thrift service is running...";
    }
}

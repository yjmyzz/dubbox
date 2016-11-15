package com.cnblogs.yjmyzz.demo.service.impl.dubbo;


import com.cnblogs.yjmyzz.demo.service.api.dubbo.DubboHelloService;
import com.cnblogs.yjmyzz.demo.service.api.dubbo.User;
import org.springframework.stereotype.Service;

/**
 * Created by yangjunming on 2016/11/3.
 */
@Service("dubboService")
public class HelloServiceImpl implements DubboHelloService {

    @Override
    public String ping() {
        return "dubbo service is running...";
    }

    @Override
    public String register(User user) {
        return String.format("%s register ok!", user.getUserName());
    }
}

package com.cnblogs.yjmyzz.demo.service.impl.avro;

import com.cnblogs.yjmyzz.demo.service.api.avro.AvroHelloService;
import org.apache.avro.AvroRemoteException;
import org.springframework.stereotype.Service;

/**
 * Created by yangjunming on 2016/11/3.
 */
@Service("avroService")
public class HelloServiceImpl implements AvroHelloService {

    @Override
    public CharSequence ping() throws AvroRemoteException {
        return "avro service is running...";
    }
}

package com.cnblogs.yjmyzz.demo.service.api.rest;

import com.cnblogs.yjmyzz.demo.service.api.dubbo.User;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Created by yangjunming on 2016/11/2.
 */
public interface RestHelloService {

    @GET
    @Path("/ping")
    String ping();

    @POST
    @Path("/reg")
    String register(User user);
}

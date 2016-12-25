package com.cnblogs.yjmyzz.demo.service.api.rest;

import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.cnblogs.yjmyzz.demo.service.api.dubbo.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by yangjunming on 2016/11/2.
 */
@Path("demo")
@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
@Produces({ContentType.APPLICATION_JSON_UTF_8, ContentType.TEXT_XML_UTF_8})
public interface RestHelloService {

    @GET
    @Path("/ping")
    String ping();

    @POST
    @Path("/reg")
    String register(User user);
}

package com.cnblogs.yjmyzz.demo.service.impl;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by yangjunming on 2016/11/3.
 */
public class DubboDemoProvider {

    public static void main(String[] args) throws InterruptedException {

        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("spring-dubbo-provider.xml");
        System.out.println("server started ...");
        while (true) {
            Thread.sleep(100);
        }
    }
}

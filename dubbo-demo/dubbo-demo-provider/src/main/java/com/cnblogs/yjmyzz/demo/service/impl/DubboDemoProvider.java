package com.cnblogs.yjmyzz.demo.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by yangjunming on 2016/11/3.
 */
public class DubboDemoProvider {

    private static Logger logger = LoggerFactory.getLogger(DubboDemoProvider.class);

    public static void main(String[] args) throws InterruptedException {

        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("spring-context.xml");

        logger.info("server started ...");

        while (true) {
            Thread.sleep(100);
        }
    }
}

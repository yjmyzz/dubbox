package com.cnblogs.yjmyzz.demo.service.api.dubbo;

import java.io.Serializable;

/**
 * Created by yangjunming on 2016/11/2.
 */
public class User implements Serializable {

    private int userId;

    private String userName;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}

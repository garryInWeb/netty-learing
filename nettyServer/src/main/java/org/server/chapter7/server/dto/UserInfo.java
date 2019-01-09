package org.server.chapter7.server.dto;

import org.msgpack.annotation.Message;

/**
 * Created by zhengtengfei on 2019/1/9.
 */
@Message
public class UserInfo {
    private String userId;
    private int number;

    public String getUserId() {
        return userId;
    }

    public int getNumber() {
        return number;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId='" + userId + '\'' +
                ", number=" + number +
                '}';
    }

    public void setNumber(int number) {
        this.number = number;
    }
}

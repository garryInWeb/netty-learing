package org.server.chapter2.nioserver;

/**
 * Created by zhengtengfei on 2018/12/29.
 */
public interface IMessageProcessor {
    public void processor(Message message, WriteProxy writeProxy);

}

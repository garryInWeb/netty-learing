package org.server.chapter2.nioserver;

import java.util.Queue;

/**
 * Created by zhengtengfei on 2018/12/29.
 */
public class WriteProxy {
    private MessageBuffer messageBuffer = null;
    private Queue writeQueue = null;

    public WriteProxy(MessageBuffer messageBuffer, Queue writeQueue) {
        this.messageBuffer = messageBuffer;
        this.writeQueue = writeQueue;
    }

    public Message getMessage(){
        return this.messageBuffer.getMessage();
    }

    public boolean enqueue(Message message){
        return this.writeQueue.offer(message);
    }
}

package org.server.chapter2.nioserver;

import java.nio.ByteBuffer;

/**
 * Created by zhengtengfei on 2018/12/29.
 */
public class Message {

    private MessageBuffer messageBuffer = null;

    public long socketId = 0;

    public byte[] shareArray = null;
    public int offset = 0;
    public int capacity = 0;
    public int length = 0;
    public Object metaData = null;


    public Message(MessageBuffer messageBuffer) {
        this.messageBuffer = messageBuffer;
    }

    public int writeToMessage(byte[] byteArray,int offset,int length){
        int remaing = length;

        while(this.length + remaing > capacity){
            if (!this.messageBuffer.expanMessage(this)){
                return -1;
            }
        }
        int bytesToCopy = Math.min(remaing,this.capacity - this.length);
        System.arraycopy(byteArray,offset,this.shareArray,this.offset + this.length,bytesToCopy);
        this.length += bytesToCopy;
        return bytesToCopy;
    }

    public int writeToMessage(byte[] byteArray){
        return writeToMessage(byteArray,0,byteArray.length);
    }

    public int writeToMessage(ByteBuffer byteBuffer){
        int remaining = byteBuffer.remaining();

        while (this.length + remaining > capacity){
            if (!this.messageBuffer.expanMessage(this)){
                return -1;
            }
        }

        int bytesToCopy = Math.min(remaining,this.capacity - this.length);
        byteBuffer.get(this.shareArray,this.offset + this.length,bytesToCopy);
        this.length += bytesToCopy;

        return bytesToCopy;
    }

    public void writePartialMessageToMessage(Message message,int endIndex){
        int startIndexOfPartialMessage = message.offset + endIndex;
        int lengthOfPartialMessage = message.offset + message.length - endIndex;

        System.arraycopy(message.shareArray,startIndexOfPartialMessage,this.shareArray,this.offset,lengthOfPartialMessage);
    }
}

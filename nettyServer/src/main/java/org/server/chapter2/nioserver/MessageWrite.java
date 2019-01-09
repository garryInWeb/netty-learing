package org.server.chapter2.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengtengfei on 2018/12/29.
 */
public class MessageWrite {

    private List<Message> writeQueue = new ArrayList<>();
    private Message messageInProgress = null;
    private int bytesWritten = 0;

    public MessageWrite() {
    }

    public void enqueue (Message message){
        if (this.messageInProgress == null){
            messageInProgress = message;
        }
        writeQueue.add(message);
    }

    public void write(Socket socket, ByteBuffer byteBuffer) throws IOException {
        byteBuffer.put(this.messageInProgress.shareArray,this.messageInProgress.offset + bytesWritten,this.messageInProgress.length - bytesWritten);
        byteBuffer.flip();

        this.bytesWritten += socket.write(byteBuffer);
        byteBuffer.clear();

        if (bytesWritten >= this.messageInProgress.length){
            if (this.writeQueue.size() > 0){
                this.messageInProgress =this.writeQueue.remove(0);
            } else{
                this.messageInProgress = null;
            }
        }
    }


    public boolean isEmpty() {
        return this.writeQueue.isEmpty() && messageInProgress ==null;
    }
}

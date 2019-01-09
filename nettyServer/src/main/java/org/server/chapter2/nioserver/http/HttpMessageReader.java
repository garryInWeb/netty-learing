package org.server.chapter2.nioserver.http;

import nioserver.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengtengfei on 2019/1/2.
 */
public class HttpMessageReader implements IMessageReader {

    private MessageBuffer messageBuffer = null;
    private List<Message> completeMessages = new ArrayList<>();
    private Message nextMessage = null;

    public HttpMessageReader() {
    }

    @Override
    public void process(Message message, WriteProxy writeProxy) {
    }

    @Override
    public void init(MessageBuffer messageBuffer) {
        this.messageBuffer = messageBuffer;
        this.nextMessage = messageBuffer.getMessage();
        this.nextMessage.metaData = new HttpHeaders();

    }

    /**
     * 从socket中读取字节放到complete
     * @param socket
     * @param readByteBuffer
     * @throws IOException
     */
    @Override
    public void read(Socket socket, ByteBuffer readByteBuffer) throws IOException {
        // 读取全部字节到 buffer 中
        int bytesRead = socket.read(readByteBuffer);
        readByteBuffer.flip();

        if (readByteBuffer.remaining() == 0){
            readByteBuffer.clear();
            return;
        }
        // 把buffer的内容写到 message中
        this.nextMessage.writeToMessage(readByteBuffer);
        // 请求体的结束标记
        int endIndex = HttpUtil.parseHttpRequest(this.nextMessage.shareArray,this.nextMessage.offset,this.nextMessage.offset + this.nextMessage.length, (HttpHeaders) this.nextMessage.metaData);
        if (endIndex != -1){
            Message message = this.messageBuffer.getMessage();
            message.metaData = new HttpHeaders();
            message.writePartialMessageToMessage(nextMessage,endIndex);

            completeMessages.add(nextMessage);
            nextMessage = message;
        }
        readByteBuffer.clear();

    }

    @Override
    public List<Message> getMessage() {
        return completeMessages;
    }
}

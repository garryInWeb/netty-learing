package org.server.chapter2.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by zhengtengfei on 2018/12/29.
 */
public interface IMessageReader {

    public void process(Message message, WriteProxy writeProxy);

    void init(MessageBuffer messageBuffer);

    void read(Socket socket, ByteBuffer readByteBuffer) throws IOException;

    public List<Message> getMessage();
}

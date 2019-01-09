package org.server.chapter1.practice01;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengtengfei on 2018/12/26.
 */
public class SimpleSocketClient {

    private static List<String> userIdList = new ArrayList<>();

    public static void main(String[] args) {
        ServerSocket socketserver = null;
        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;
        // 服务器端地址
        String serverIP = "10.100.99.142";
        // 服务器端口号
        int port = 9898;
        // 发送内容
        String data = "Hello";

        try {
            socketserver = new ServerSocket(port);
            // 发送数据

            while (true){
                socket = socketserver.accept();
                new LogicThread(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
                socketserver.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

//    public static void userIdList() throws IOException {
//        Path path = Paths.get("C:\\Users\\zhengtengfei\\Desktop\\userIdList.txt");
//        path.normalize();
//        FileChannel fileChannel = FileChannel.open(path);
//        Selector selector = Selector.open();
//        ByteBuffer byteBuffer = ByteBuffer.allocate(38);
//        int byteReader = fileChannel.read(byteBuffer);
//        long x = System.currentTimeMillis();
//
//        while (byteReader != -1){
////            System.out.println("Read:" + byteBuffer);
//            byteBuffer.flip();
//            StringBuilder userId = new StringBuilder();
//            while (byteBuffer.hasRemaining()){
//                userId.append((char) byteBuffer.get());
//            }
//            userIdList.add(userId.toString());
//            byteBuffer.clear();
//            byteReader = fileChannel.read(byteBuffer);
//        }
//        System.out.println();
//        System.out.println(System.currentTimeMillis() - x);
//    }
//    public static String getSignRandom() throws IOException {
//        if (userIdList == null || userIdList.isEmpty()){
//            userIdList();
//        }
//        Random rand = new Random();
//        int line = rand.nextInt(userIdList.size());
//        return userIdList.get(line);
//    }
//
//    public static void main(String[] args) throws IOException {
//        System.out.println(getSignRandom());
//    }

}

package org.server.chapter1.practice01;

import java.io.*;
import java.net.Socket;

/**
 * Created by zhengtengfei on 2018/12/27.
 */
public class SimpleSocketC {

    public static String data = "";

    public static void main(String[] args) throws FileNotFoundException {

        final Socket[] socket = {null};

        final InputStream[] is = {null};

        final OutputStream[] os = {null};

        //服务器端IP地址

        String serverIP = "10.100.99.142";

        //服务器端端口号

        int port = 9898;

        userIdList();

        //发送内容

        try {
            for (int i = 0; i < 10 ; i++) {
                new Thread(() -> {
                    try {
                        //建立连接
                        socket[0] = new Socket(serverIP, port);
                        //发送数据
//                        Thread.sleep(2000);
                        os[0] = socket[0].getOutputStream();

                        os[0].write(data.getBytes());

                        //接收数据

                        is[0] = socket[0].getInputStream();

                        byte[] b = new byte[1024];

                        int n = is[0].read(b);

                        //输出反馈数据

                        System.out.println("服务器反馈：" + new String(b, 0, n));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }).start();
            }

        } catch (Exception e) {

            e.printStackTrace(); //打印异常信息

        }finally{

            try {

                //关闭流和连接

                is[0].close();

                os[0].close();

                socket[0].close();

            } catch (Exception e2) {}

        }

    }
    public static void userIdList() throws FileNotFoundException {
        File resource = new File("C:\\Users\\zhengtengfei\\Desktop\\userIdList.txt");
        FileInputStream fileInputStream = new FileInputStream(resource);
        BufferedReader br = null;
        try {
            String s1;
            br = new BufferedReader(new InputStreamReader(fileInputStream,"utf-8"));
            while ((s1 = br.readLine()) != null) {
                data = data + s1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

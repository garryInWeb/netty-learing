package org.server.chapter1.practice01;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by zhengtengfei on 2018/12/26.
 */
public class InetAddressDemo {
    public static void main(String[] args) {
        try {
            // 域名创建对象
            InetAddress inet1 = InetAddress.getByName("www.baidu.com");
            System.out.println(inet1);

            // IP创建对象
            InetAddress inet2 = InetAddress.getByName("10.100.99.142");
            System.out.println(inet2);

            // 本机对象
            InetAddress inet3 = InetAddress.getLocalHost();
            System.out.println(inet3);

            // 对象中存储的域名
            String hostName = inet3.getHostName();
            System.out.println(hostName);

            // 对象中存储的IP
            String ip = inet3.getHostAddress();
            System.out.println(ip);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}

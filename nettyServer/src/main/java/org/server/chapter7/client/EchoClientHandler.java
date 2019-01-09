package org.server.chapter7.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.server.chapter7.server.dto.UserInfo;


/**
 * Created by zhengtengfei on 2019/1/9.
 */
public class EchoClientHandler extends ChannelHandlerAdapter {

    private final int sendNumber;
    private int count;

    public EchoClientHandler(int sendNumber) {
        this.sendNumber = sendNumber;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        UserInfo info = new UserInfo();
//        info.setNumber(1);
//        info.setUserId("a");
//        ctx.writeAndFlush(info);
        UserInfo[] userInfos = UserInfos();
        for (UserInfo info : userInfos){
            ctx.write(info);
        }
        ctx.flush();
    }

    private UserInfo[] UserInfos() {
        UserInfo[] userInfos = new UserInfo[sendNumber];
        UserInfo userInfo = null;
        for (int i =0 ;i < sendNumber; i++){
            userInfo = new UserInfo();
            userInfo.setUserId("D " + i);
            userInfo.setNumber(i);
            userInfos[i] = userInfo;
        }
        return userInfos;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Client receive " + msg + ";count " + ++count);
        if (count < 5)
            ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}

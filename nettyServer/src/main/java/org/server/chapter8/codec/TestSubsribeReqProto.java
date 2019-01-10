package org.server.chapter8.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import org.server.chapter8.protobuf.SubscribeReqProto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengtengfei on 2019/1/10.
 */
public class TestSubsribeReqProto {

    private static byte[] encode(SubscribeReqProto.SubscribeReq req){
       return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }

    private static SubscribeReqProto.SubscribeReq createSubscribeReq(){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(1);
        builder.setUserName("abc");
        builder.setProductName("good");
        List<String> address = new ArrayList<>();
        address.add("NAN JING YUHUATAI");
        address.add("NAN JING YUHUATAIS");

        builder.addAllAddress(address);
        return builder.build();
    }

    public static void main(String[] args) throws InvalidProtocolBufferException {
        SubscribeReqProto.SubscribeReq req = createSubscribeReq();
        System.out.println("Before req"  + req.toString());
        SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
        System.out.println("After decode : " + req.toString());
        System.out.println("Assert equal :" + req2.equals(req));

    }
}

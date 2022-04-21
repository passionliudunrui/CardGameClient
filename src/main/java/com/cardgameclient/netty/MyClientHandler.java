package com.cardgameclient.netty;


import com.cardgameclient.proto.MessagePOJO;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Service;

@Service
@ChannelHandler.Sharable
public class MyClientHandler extends ChannelInboundHandlerAdapter {

    private MyClient myClient;

    public MyClientHandler(MyClient myClient){
        this.myClient=myClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MessagePOJO.Message message=MessagePOJO.Message.newBuilder().setId1(1).setId2(2).setId3(3).setContext("你好").build();
        ctx.writeAndFlush(message);
        System.out.println("发送消息完成");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessagePOJO.Message message=MessagePOJO.Message.newBuilder().setId1(1).setId2(2).setId3(3).setContext("12").build();
        ctx.writeAndFlush(message);
    }
}

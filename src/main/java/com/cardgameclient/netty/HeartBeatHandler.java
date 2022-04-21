package com.cardgameclient.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Service;


@Service
@ChannelHandler.Sharable
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {



    }



    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt){



    }


}

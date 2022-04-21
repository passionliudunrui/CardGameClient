package com.cardgameclient.netty;

import com.cardgameclient.proto.MessagePOJO;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MyClient {

    private final String HOST="127.0.0.1";
    private final int PORT=8888;

    private Channel channel;
    private EventLoopGroup workerGroup=new NioEventLoopGroup(1);
    private Bootstrap bootstrap;

    @Autowired
    private HeartBeatHandler heartBeatHandler;



    public void start(){

         bootstrap=new Bootstrap();

        try{

            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            //bootstrap.handler(new MyClientInitializer(MyClient.this));

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline=socketChannel.pipeline();

                    pipeline.addLast(new ProtobufVarint32FrameDecoder());
//                    pipeline.addLast("decoder",
//                            new ProtobufDecoder(Message.MyMessage.getDefaultInstance()));
                    pipeline.addLast("decoder",new ProtobufDecoder(
                            MessagePOJO.Message.getDefaultInstance()
                    ));

                    pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                    pipeline.addLast(new ProtobufEncoder());

                    //pipeline.addLast(new IdleStateHandler(5,0,0, TimeUnit.SECONDS));
                    //pipeline.addLast(heartBeatHandler);

                    pipeline.addLast(new MyClientHandler(MyClient.this));

                }
            });


            doConnect();

        }catch (Exception e){
            System.out.println("客户端启动异常");
            e.printStackTrace();
        }

    }


    public void destory() {
        workerGroup.shutdownGracefully();

    }

    protected void doConnect() {

        if (channel != null && channel.isActive()) {
            return;
        }

        ChannelFuture future = bootstrap.connect(HOST, PORT);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    System.out.println("Connect to server successfully!");
                } else {
                    System.out.println("Failed to connect to server, try connect after 10s");
                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });

    }


}

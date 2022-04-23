package com.cardgameclient.netty;


import com.cardgameclient.proto.MessagePOJO;
import com.cardgameclient.thread.WaitingThread;
import com.cardgameclient.utils.MD5Util;
import com.cardgameclient.utils.Tools;
import com.cardgameclient.utils.Transfrom;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@ChannelHandler.Sharable
public class MyClientHandler extends SimpleChannelInboundHandler<MessagePOJO.Message> {

    private MyClient myClient;

    public MyClientHandler(MyClient myClient){
        this.myClient=myClient;
    }

    private ChannelHandlerContext ctx;
    private Scanner scanner=new Scanner(System.in);
    private static int count=0;
    private static int failNum=0;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
        Tools.display2();

        int choice=scanner.nextInt();

        if(choice==1){
            apply();
        }
        else if(choice==2){
            register();
        }
        else{
            System.out.println("输入错误，请重新输入");
        }

    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        MessagePOJO.Message message=MessagePOJO.Message.newBuilder().setId1(1).setId2(2).setId3(3).setContext("12").build();
//        ctx.writeAndFlush(message);
//    }
//


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessagePOJO.Message message) throws Exception {

        int id1=message.getId1();//哪个模块
        int id2=message.getId2();//模块对应的消息
        String context=message.getContext();

        String[]data=context.split(",");

        switch (id1){
            case 1:

                log.info("服务端返回注册消息");
                handlerApply(id2,context);
                break;
            case 2:

                log.info("服务端返回登录信息");
                judgeLog(id2,context);
                break;
            case 3:
                log.info("服务端返回历史记录信息");
                printMessage(context);
                break;
            case 4:
                log.info("");


        }


    }


    /**
     * 显示服务器返回的消息
     * @param context
     */
    private void printMessage(String context) {

        System.out.println(context);

        write();
    }


    /**
     * 注册模块
     */
    private void apply(){
        System.out.println("请输入手机号 昵称  密码");
        String id=scanner.next();
        String nickName=scanner.next();
        String paswd=scanner.next();
        String password = MD5Util.md5(paswd);
        System.out.println("加密后 "+password);

        String context=id+","+nickName+","+password;
        MessagePOJO.Message message= Transfrom.transform(1,context);

        ctx.writeAndFlush(message);
    }

    /**
     * 处理服务端返回的注册消息
     * @param id2
     * @param context
     */
    private void handlerApply(int id2,String context){

        if(id2==1){
            System.out.println("申请账号成功");
        }
        else{
            System.out.println("申请账号失败");
        }

        Tools.display2();

        int choice=scanner.nextInt();

        if(choice==1){
            apply();
        }
        else if(choice==2){
            register();
        }
        else{
            System.out.println("输入错误，请重新输入");
        }

    }

    /**
     * 登录模块
     */
    private void register(){
        System.out.println("请输入登录的账号和密码");
        String id=scanner.next();
        String paswd=scanner.next();

        String password = MD5Util.md5(paswd);
        System.out.println("加密后 "+password);

        String context=id+","+password;
        MessagePOJO.Message message = Transfrom.transform(2, context);

        ctx.writeAndFlush(message);
    }

    /**
     * 验证登录模块
     */
    private void judgeLog(int id2,String context) {
        if(id2==1){
            System.out.println("登录游戏成功");
            //进行下一步操作
            System.out.println("成功进入游戏大厅");
            write();

        }
        else{
            System.out.println("登录失败，请重新输入账号密码");
            failNum++;
            if(failNum>=3){
                System.out.println("------------------------------请稍后登录-----------------------------");

                //发送一个消息给服务端，让服务端把通道关闭

                ScheduledExecutorService scheduledExecutorService=new ScheduledThreadPoolExecutor(1);
                scheduledExecutorService.scheduleAtFixedRate(new WaitingThread(5),0,1, TimeUnit.SECONDS);

                try{
                    Thread.sleep(5000);
                    scheduledExecutorService.shutdown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("请输入登录的账号和密码");
            String id=scanner.next();
            String passwd=scanner.next();
            String password=MD5Util.inputPassToFromPass(passwd);
            String cotext1=id+","+password;

            MessagePOJO.Message message = Transfrom.transform(2, cotext1);

            ctx.writeAndFlush(message);
        }

    }

    /**
     * 用户进入游戏的选择按钮
     */
    private void write(){
        Tools.display();
        int choice=scanner.nextInt();

        switch (choice){
            case 3:
                sendMessage(3);
                break;
            case 4:
                sendMessage(4);
                break;
            case 5:
                sendMessage(5);
                break;
            case 6:
                sendMessage(6);
                break;
            case 7:
                sendMessage(7);
                break;
            case 8:
                sendMessage(8);
                break;
        }




    }

    private void sendMessage(int id1){

        System.out.println("发送消息给服务端");
        MessagePOJO.Message message = Transfrom.transform(id1, "");
        ctx.writeAndFlush(message);

    }


























}

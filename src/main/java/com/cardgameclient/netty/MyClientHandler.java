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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    public static long TimeDifference;
    public static long time1;//开始发送消息的时间
    public static long startTime;
    public static long endTime;

    /**
     * 客户端启动的时候 同步客户端和服务端的时间  获取到客户端和服务端的差值
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
        System.out.println("记录当前时间");
        Long time1=new Date().getTime();
        this.time1=time1;
        System.out.println("正在同步信息");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessagePOJO.Message message) throws Exception {

        int id1=message.getId1();//哪个模块
        int id2=message.getId2();//模块对应的消息
        int id3=message.getId3();
        String context=message.getContext();

        String[]data=context.split(",");

        switch (id1){
            case 0:
                log.info("服务端发来同步信息的信息");
                syncTime(context);
                break;
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
                log.info("服务端返回TopTen");
                printMessage(context);
                break;
            case 5:
                log.info("服务端返回加入游戏结果");
                showAnswer(context);
                break;
            case 6:
                log.info("服务端返回开始游戏的结果");
                playGame(id2,id3,context);
                break;
            case 8:
                log.info("服务端返回抢购信息");
                rushBuyHappyBean(id2,context);
                break;
            case 9:
                log.info("服务端返回购买欢乐豆的信息");
                buyHappyBean(id2,context);
                break;
            case 10:
                log.info("服务端返回更新用户信息的结果");
                printMessage(context);
                break;
            case 12:
                log.info("服务端返回抢购欢乐豆结果");
                printMessage(context);
                break;


        }

    }


    //-------------------------------------客户端层面  C-->S---------------------------------------------

    /**
     * 主菜单选项
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
                secKill(8);
                break;
            case 10:
                modifyInfo();
                break;
            case 9:
                sendMessage(9,1);
                break;
            case 11:
                sendMessage(11);
                break;
            case 12:
                sendMessage(12);
                break;
        }
    }

    /**
     * 发送消息模块
     * @param id1
     */

    private void sendMessage(int id1){

        System.out.println("发送消息给服务端");
        MessagePOJO.Message message = Transfrom.transform(id1, "");
        ctx.writeAndFlush(message);

    }
    /**
     * 发送消息重载
     */
    private void sendMessage(int id1,int id2){
        System.out.println("发消息给服务端");
        MessagePOJO.Message message = Transfrom.transform(id1, id2, "");
        ctx.writeAndFlush(message);
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
     * 修改用户信息模块
     */
    private void modifyInfo() {
        System.out.println("请输入新的用户名和密码");
        String nickName=scanner.next();
        String passwd=scanner.next();
        String password = MD5Util.md5(passwd);

        MessagePOJO.Message message1 = Transfrom.transform(10, 1, nickName + "," + password);

        ctx.writeAndFlush(message1);
    }



    /**
     * 用户选择出牌逻辑
     */
    public void choose(){
        System.out.println("请输入你选择的牌 或者输入不要");
        String poker=scanner.next();
        MessagePOJO.Message message1;
        if(poker.equals("不要")){
            message1=Transfrom.transform(6, 0, poker);
        }
        else{
            message1 = Transfrom.transform(6, 1, poker);
        }

        ctx.writeAndFlush(message1);
    }



    /**
     * 秒杀模块  模拟现在就可以抢购
     * @param id1
     */
    private void secKill(int id1) {
//        long nowTime=new Date().getTime();
//        long serverTime=nowTime+TimeDifference;
//        //客户端对时间进行拦截
//        if(nowTime+TimeDifference<startTime){
//            System.out.println("本周六秒杀活动还未开始");
//            System.out.println("还有"+(startTime-nowTime)/1000+"秒钟开始秒杀");
//            write();
//        }
//        else if(nowTime+TimeDifference>endTime){
//            System.out.println("本周六秒杀活动已经结束");
//            write();
//        }


//        else{
            System.out.println("秒杀活动已经开始");
            System.out.println("是否抢购商品？  1是抢购  2不抢购");
            String choice=scanner.next();

            if(choice.equals("2")){
                write();
            }else{
                MessagePOJO.Message message1 = Transfrom.transform(8, choice);
                ctx.writeAndFlush(message1);
            }

//        }

    }


    //-------------------------------------服务端层面  S-->C---------------------------------------------
    /**
     * 0
     * 同步服务器和客户端的时间信息
     * @param context
     */
    private void syncTime(String context) {
        String[]data=context.split(",");
        Long time2=Long.valueOf(data[0]);
        Long newTime=new Date().getTime();

        this.TimeDifference=(time2-time1+time2-newTime)/2;
        this.startTime=Long.valueOf(data[1]);
        this.endTime=Long.valueOf(data[2]);
        System.out.println(TimeDifference);
        System.out.println("开始时间"+startTime);
        System.out.println("结束时间"+endTime);

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
     * 1
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
     * 2
     * 验证登录模块
     * @param id2
     * @param context
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
            String password=MD5Util.md5(passwd);
            String cotext1=id+","+password;

            MessagePOJO.Message message = Transfrom.transform(2, cotext1);

            ctx.writeAndFlush(message);
        }

    }

    /**
     * 3
     * 显示服务器返回的消息
     * @param context
     */
    private void printMessage(String context) {
        System.out.println(context);
        write();
    }

    /**
     * 5
     * 返回服务端加入游戏的结果
     * @param context
     */
    private void showAnswer(String context) {
        System.out.println(context);
        System.out.println("................................");
    }


    /**
     * 6
     * 打牌的整个业务
     * @param  id3
     * @param id2
     * @param context
     */
    private void playGame(int id2, int id3,String context) {
        if(id2==0){
            if(id3==0){
                //匹配成功加入游戏
                System.out.println(context);
            }
            else if(id3==1){
                //先出牌
                System.out.println(context);
                choose();
            }
            else if(id3==2){
                //等待对方出牌
                System.out.println(context);
            }
            else{
                //id3=4  对方不要 继续出牌
                System.out.println(context);
                choose();
            }

        }
        else if(id2==1){
            System.out.println(context);
            choose();
        }
        else if(id2==2){
            System.out.println(context);
            write();
        }
        else{
            System.out.println("服务端发送的信息错误");
        }
    }

    /**
     *
     * 8
     * 服务端返回抢购商品的信息
     * @param id2
     * @param context
     */
    private void rushBuyHappyBean(int id2, String context) {
        if(id2==1){
            System.out.println(context);
            write();

        }
        else{
            System.out.println(context);
            System.out.println("查看购买结果 请在主菜单输入12");
            write();
        }

    }



    /**
     * 9
     * 处理购买欢乐豆的信息
     * @param id2   0表示余额不足
     * @param context
     */
    private void buyHappyBean(int id2, String context) {
        //完成
        if(id2==1){
            System.out.println(context);
            write();
        }
        //失败
        else if(id2==2){
            System.out.println(context);;
            write();
        }

    }

}

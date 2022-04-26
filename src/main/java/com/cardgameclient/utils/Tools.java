package com.cardgameclient.utils;

public class Tools {

    /**
     * 关于购买欢乐豆和秒杀欢乐豆的说明
     *
     * 购买：
     * 什么时候都能发生  20yuan  100happybean
     * 1.用户发送9,1 下单操作
     * 2.服务端返回9,1 客户端下单成功 并且询问是否支付
     * 3. 现在支付9,2   稍后支付9,3   取消支付9,4
     * 4.服务端进行处理(消息队列)
     *

     * 抢购：
     *
     *
     * 每周五晚上8:00   10yuan  100happybean   前100名有机会获得
     *
     *
     */

    public static void display(){
        System.out.println("请输入你选择的功能" +
                "3.查看游戏历史记录"+
                "4.查看排行榜"+
                "5.加入游戏"+
                "7.退出游戏"+
                "8.秒杀欢乐豆"+
                "9.购买欢乐豆"+
                "10.修改用户信息");
    }

    public static void display2(){
        System.out.println("请输入你选择的功能" +
                "1.注册" +
                "2.登录");
    }




}

package com.cardgameclient.thread;

public class WaitingThread implements Runnable{

    private int second;


    public WaitingThread(int second) {
        this.second = second;

    }

    @Override
    public void run(){

        System.out.println("还需要等待"+(second--)+"秒钟");

    }
}
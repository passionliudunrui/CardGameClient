package com.cardgameclient;

import com.cardgameclient.netty.MyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CardGameClientApplication implements CommandLineRunner {

    @Autowired
    private MyClient myClient;

    public static void main(String[] args) {
        SpringApplication.run(CardGameClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        myClient.start();

        Runtime.getRuntime().addShutdownHook(
                new Thread(()->myClient.destory())
        );

    }
}

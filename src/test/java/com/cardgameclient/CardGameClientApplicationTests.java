package com.cardgameclient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CardGameClientApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void test1(){
        String s="";
        String[] split = s.split(",");
        for(String s1:split){
            System.out.println(s1);
            System.out.println();
        }
        System.out.println(split.length);

    }

}

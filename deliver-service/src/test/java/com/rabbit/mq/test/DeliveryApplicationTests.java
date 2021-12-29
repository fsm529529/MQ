package com.rabbit.mq.test;

import com.rabbit.mq.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DeliveryApplicationTests {
    @Autowired
    DeliveryService deliveryService;
    @Test
    void contextLoads() {
        System.out.println("hello");
    }
    @Test
    public void deliveryTest(){
        try {
            deliveryService.saveDelivery("zt10001");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.rabbit.mq.test;

import com.rabbit.mq.entity.Order;
import com.rabbit.mq.service.MQService;
import com.rabbit.mq.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class OrderApplicationTests {
    @Autowired
    OrderService orderService;
    @Autowired
    MQService mqService;

    @Test
    void contextLoads() {
        System.out.println("hello");
    }

    @Test
    public void saveOrder() throws Exception {
        Order order = new Order();
        order.setOrderId("2000ZT");
        order.setOrderContent("测试订单");
        order.setUserId(1);
        order.setCreateTime(new Date());
        orderService.saveOrder(order);
        mqService.sendMessge(order);
        Thread.sleep(300000);
    }

}

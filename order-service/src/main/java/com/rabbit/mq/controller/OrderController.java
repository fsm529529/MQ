package com.rabbit.mq.controller;

import com.rabbit.mq.entity.Order;
import com.rabbit.mq.service.MQService;
import com.rabbit.mq.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shermanfu
 * @Description: 订单类controller入口
 * @date 2021/12/12 21:14
 */
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    MQService orderMQService;

    @PostMapping("/saveOrder")
    public void saveOrder(@RequestBody Order order) {
        try {
            //1. 将产生的订单信息放入本地订单数据库（包括冗余订单数据也放入本地数据库）
            orderService.saveOrder(order);
            //2. 将订单消息发送给MQ，以便后续可以被货物配送中心消费
            orderMQService.sendMessge(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

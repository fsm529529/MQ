package com.rabbit.mq.controller;

import com.rabbit.mq.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author shermanfu
 * @Description: 订单类controller入口
 * @date 2021/12/12 21:14
 */
@RestController
public class DeliveryController {
    @Autowired
    DeliveryService deliveryService;

    @RequestMapping("/saveDelivery")
    public String saveOrder(@RequestParam("orderId") String orderId) {
        String result = "";
        try {
            result = deliveryService.saveDelivery(orderId);
        } catch (Exception e) {
            System.out.println("创建运单失败");
        }
        return result;
    }
}

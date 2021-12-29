package com.rabbit.mq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;


/**
 * @author shermanfu
 * @Description: 订单服务类
 * @date 2021/12/12 21:15
 */
@Service
public class DeliveryService {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 运单接收
     *
     * @param orderId
     */
    @Transactional(rollbackFor = Exception.class)
    public String saveDelivery(String orderId) throws Exception {
        String sql = "insert into my_delivery(delivery_id,order_id,status,order_content,user_id,create_time) values(?,?,?,?,?,?)";
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        int count = jdbcTemplate.update(sql, uuid, orderId, 0, "test", 1, new Date());
        if (count != 1) {
            System.out.println("创建运单失败");
            throw new Exception("error,创建运单失败");
        }
        return uuid;
    }
}

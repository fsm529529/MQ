package com.rabbit.mq.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.rabbit.mq.entity.Order;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author shermanfu
 * @Description: MQ发送消息service, 用于将生产端产生的订单消息发送给MQ服务器
 * @date 2021/12/24 20:21
 */
@Service
public class MQService {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostConstruct//该注解用在非静态void方法上，在容器spring容器启动成功后初始化
    public void callback() {//发送消息给MQ的的回调函数
        //每次消息发送MQ成功后，给予生产者的消息回执，来确保生产者的可靠性
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                //ack为true代表MQ已经收到消息
                System.out.println("cause=" + cause);
                String orderId = correlationData.getId();//sendMessge()方法中设置的orderId
                if (!ack) {
                    //这里可能要进行其他方式的存储处理
                    System.out.println("MQ应答失败：orderId=" + orderId);
                    return;
                }
                try {
                    String sql = "update order_message set status=1 where order_id=?";
                    jdbcTemplate.update(sql, orderId);

                } catch (Exception e) {
                    System.out.println("修改订单数据库本地数据状态出现异常，原因：" + e.toString());
                }

            }
        });

    }

    public void sendMessge(Order order) {
        //通过MQ发送消息,在调用测试类的方法时：先通过MQ界面的方式创建名称为order_fanout_exchange、类型为fanout的exchange以及名称为order_queue的队列名称并将2者通过界面进行绑定

        rabbitTemplate.convertAndSend("order_fanout_exchange", "", JSON.toJSONString(order),
                new CorrelationData(order.getOrderId()));
    }

    //使用定时器扫描生产端投递MQ失败的消息，然后进行消息的重发,以此来保证可靠生产，注意订单在消费时的幂等性问题，可以通过redis锁解决
    /*@Scheduled(cron = "xxxx")
    public void sendFailedMessage2MQ() {
        String sql = "select *From my_order  where order_id in( select order_id from order_message where status=0)";
        List<Order> orders = jdbcTemplate.queryForList(sql, Order.class);
        if (null != orders && orders.size() > 0) {
            //消息重新投递
            for (Order order : orders) {
                sendMessge(order);
            }
        }

    }*/

}

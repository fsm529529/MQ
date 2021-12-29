package com.rabbit.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.rabbit.mq.entity.Order;
import com.rabbit.mq.service.DeliveryService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

;

/**
 * @author shermanfu
 * @Description: order.queue的死信队列dead.order.queue的消费者，该类主要用于获取因为MQConsumer类中消费订单消息失败而
 * 被打入死信队列中的消息，整体逻辑与MQConsumer类似，只是在发生异常，此时应该进行预警处理
 * @date 2021/12/24 21:43
 */
@Service
public class DeadLetterMQConsumer {
    @Autowired
    DeliveryService deliveryService;
    private int count = 0;

    @RabbitListener(queues = {"dead.order.queue"})
    public void consumeOrderMessage(String orderMessge, Channel channel, CorrelationData correlationData,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        try {
            //1. 获取消息队列的消息
            System.out.println("收到MQ消息队列的订单消息是：" + orderMessge + ",总数count=" + count++);
            //2.将订单消息反序列化得到订单
            Order order = JSON.parseObject(orderMessge, Order.class);
            //3.获取订单ID
            String orderId = order.getOrderId();
            //4.派单处理，要考虑幂等性问题：可以通过数据库主键进行，也可以通过分布式锁进行
            deliveryService.saveDelivery(orderId);
            //int i = 1 / 0;//消费者出现异常后会不断进行消费的重试，没有考虑这点容易把服务器资源耗尽
            //5. 手动ack告诉MQ消息已经正常消费
            channel.basicAck(tag, false);//false一次只确认一条消息，true一次迭代循环确认多条消息
        } catch (Exception e) {
            System.out.println("进行人工干预");
            System.out.println("发送报警");
            System.out.println("将异常消息移送到别的DB进行存储");
            channel.basicNack(tag, false, false);//将死信队列中的消息进行移除，否则会造成死信队列消息的堆积
        }

    }
}

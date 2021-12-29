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
;import java.io.IOException;

/**
 * @author shermanfu
 * @Description:
 * @date 2021/12/24 21:43
 */
@Service
public class MQConsumer {
    @Autowired
    DeliveryService deliveryService;
    private int count = 0;

    //解决消费者在出现异常情况下会进行消息不断重试的几种解决方案：
    //1.控制重发次数（配置见yml文件中的 enabled: true #开启重试 max-attempts: 3 #重试最大次数 ）+死信队列
    //2.try+catch+手动ack
    //3.try+catch+手动ack+死信队列+人工干预
    //4.
    @RabbitListener(queues = {"order.queue"})
    public void consumeOrderMessage(String orderMessge, Channel channel, CorrelationData correlationData,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        try {
            //1. 获取消息队列的消息
            System.out.println("收到MQ消息队列的订单消息是：" + orderMessge + ",总数count=" + count++);
            //2.将订单消息反序列化得到订单
            Order order = JSON.parseObject(orderMessge, Order.class);
            //Order order = JSON.parseObject(orderMessge, Order.class);
            //3.获取订单ID
            String orderId = order.getOrderId();
            //4.派单处理，要考虑幂等性问题：可以通过数据库主键进行，也可以通过分布式锁进行
            deliveryService.saveDelivery(orderId);
            //int i = 1 / 0;//消费者出现异常后会不断进行消费的重试，没有考虑这点容易把服务器资源耗尽
            //5. 手动ack告诉MQ消息已经正常消费
            channel.basicAck(tag, false);//false一次只确认一条消息，true一次迭代循环确认多条消息
        } catch (Exception e) {
            //basicNack(long deliveryTag, boolean multiple, boolean requeue)
            //参数1: 消息tag ; 参数2：是否多条消息进行迭代ack; 参数3:是否进行消息重发；
            //参数3如果为false则消息不会重发直接被打入死信队列；为true的话消息会重发，此时不建议使用try catch,否则会造成死循环（出错的这条消息会不断被发送到队列然后又再次被消费）
            //如果出现异常后，是重发一次还是记录日志或者存入库中要根据实际业务场景自己决定
            channel.basicNack(tag, false, false);//发生异常消息消费失败就会把该条消息放入指定的死信队列，因为参数3 requeue为false（死信队列也是很普通的一种队列，只是在队列配置文件中指定了order队列与死信队列的绑定关系，仅此而已）
        }

    }
}

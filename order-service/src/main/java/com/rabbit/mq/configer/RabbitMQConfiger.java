package com.rabbit.mq.configer;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shermanfu
 * @Description: mq订单队列的死信队列配置类，订单队列消费失败后会将失败的消息投递到死信队列以便后续的处理（保证失败的消息不被系统丢弃）
 * 将订单队列与死信队列进行绑定
 * @date 2021/12/12 21:21
 *
 * 该类已经放在生产端order-service中，放在消费端代码中直接运行会报错，队列的配置在消费和生产端只要有
 * 一方配置了就可以，同时也可以通过MQ的图形化界面提前进行配置
 */
@Configuration
public class RabbitMQConfiger {
    //定义死信队列相关
    @Bean
    public FanoutExchange deadExchange() {
        return new FanoutExchange("dead_order_fanout_exchange", true, false);
    }

    @Bean
    public Queue deadOrderQueue() {
        return new Queue("dead.order.queue", true);
    }

    @Bean
    public Binding bindDeadOrderQueue() {
        return BindingBuilder.bind(deadOrderQueue()).to(deadExchange());
    }

    //定义order队列相关并绑定死信队列
    @Bean
    public FanoutExchange orderFanoutExchange() {
        return new FanoutExchange("order_fanout_exchange", true, false);
    }

    @Bean
    public Queue orderQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "dead_order_fanout_exchange");//绑定dead.order.queue的交换机dead_order_fanout_exchange
        return new Queue("order.queue", true,false,false,args);
    }
    @Bean
    public Binding bindOrderQueue() {
        return BindingBuilder.bind(orderQueue()).to(orderFanoutExchange());
    }
}

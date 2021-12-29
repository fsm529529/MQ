package com.rabbit.mq.service;

import com.rabbit.mq.entity.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * @author shermanfu
 * @Description: 订单服务类
 * @date 2021/12/12 21:15
 */
@Service
public class OrderService {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 消息生产端将订单数据、订单冗余数据保存到order数据库中
     *
     * @param order
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrder(Order order) throws Exception {
        String sql = "insert into my_order(order_id,user_id,order_content,create_time) values(?,?,?,?)";
        int count = jdbcTemplate.update(sql, order.getOrderId(), order.getUserId(), order.getOrderContent(), order.getCreateTime());
        if (count != 1) {
            throw new Exception("创建订单失败");
        }
        /**该方法是正常情况下不采用MQ的方式来进行跨系统HTTP方式调用运单中心来将订单中心的订单ID
         * 传递给运单中心以便运单中心就行货物配送，这种方式的缺点是：存在分布式事务，假如运单中心插入数据库成功了，
         * 但是因为网络等原因造成http返回connection timeout，就会导致saveOrder方法内部出现异常，从而导致
         * 数据在插入订单中心时回滚，而此时运单中心却插入成功了，这就造成了事务不一致的问题。
         String result = dispatcherAPI(order.getOrderId());
         if (!StringUtils.isEmpty(result)) {
         System.out.println("成功将订单同步到配送中心！！！");
         }**/
        //在冗余表里插入一条数据进行消息冗余
        saveLocalMessage(order);
    }

    /**
     * 本方法的目的是：订单中心在订单数据库中插入一条数据的同时，会调用saveLocalMessage在订单数据库同一个库
     * 中插入一条数据到order_message表中作为冗余数据（数据状态位0），目的是：一但消息发送到MQ时出现失败，则可以通过
     * 定时任务扫描order_message表中status为0的数据，然后进行数据重发；若消息发送MQ成功，则：通过MQ的回调机制将
     * order_message表中status状态更新为1，这样就能保证发送端可靠生产问题，从而实现柔性事务。
     *
     * @param order
     */
    public void saveLocalMessage(Order order) throws Exception {
        String sql = "insert into order_message(order_id,status,order_content,unique_id) values(?,?,?,?)";
        int count = jdbcTemplate.update(sql, order.getOrderId(), 0, order.getOrderContent(), "xxxx");
        if (count != 1) {
            throw new Exception("插入订单冗余表表失败");
        }
    }

    /**
     * 通过http的方式调用远程配送中心的服务将订单传送给配送中心
     *
     * @param orderId
     * @return
     */
    private String dispatcherAPI(String orderId) {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        //链接超时时间
        simpleClientHttpRequestFactory.setConnectTimeout(3000);
        //处理超时时间
        simpleClientHttpRequestFactory.setReadTimeout(2000);
        //配送中心服务地址
        String url = "http://localhost:9000/saveDelivery?orderId=" + orderId;
        RestTemplate restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
        String result = restTemplate.getForObject(url, String.class);
        return result;
    }
}

package com.rabbit.mq.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author shermanfu
 * @Description: 订单实体类
 * @date 2021/12/12 21:19
 */
public class Order implements Serializable {
    String orderId;
    Integer userId;
    String orderContent;
    Date createTime;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOrderContent() {
        return orderContent;
    }

    public void setOrderContent(String orderContent) {
        this.orderContent = orderContent;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userId=" + userId +
                ", orderContent='" + orderContent + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}

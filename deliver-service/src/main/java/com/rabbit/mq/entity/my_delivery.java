package com.rabbit.mq.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author shermanfu
 * @Description: 配送实体类
 * @date 2021/12/12 21:19
 */
public class my_delivery implements Serializable {
    String deliveryId;
    String orderId;
    Integer status;
    String orderContent;
    Integer userId;
    Date createTime;

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrderContent() {
        return orderContent;
    }

    public void setOrderContent(String orderContent) {
        this.orderContent = orderContent;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "my_delivery{" +
                "deliveryId='" + deliveryId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", status=" + status +
                ", orderContent='" + orderContent + '\'' +
                ", userId=" + userId +
                ", createTime=" + createTime +
                '}';
    }
}

package com.increff.Model;

import com.increff.Constants.Status;
import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity(name = "Toy_Order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private Long clientId;
    private Long customerId;
    private Long channelId;
    @NotNull
    @Column(unique = true)
    private String channelOrderId;
    @Enumerated(value = EnumType.STRING)
    private Status status;

    public Order() {
    }

    public Order(Long clientId, Long customerId, Long channelId, String channelOrderId, Status status) {
        this.clientId = clientId;
        this.customerId = customerId;
        this.channelId = channelId;
        this.channelOrderId = channelOrderId;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public void setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}


package com.increff.Model;

import com.increff.Constants.Status;

public class OrderDto {
    private Long clientId;
    private Long customerId;
    private Long channelId;
    private String channelOrderId;
    private Status status;
    
    public OrderDto() {
    }
    
    public OrderDto(Long clientId, Long customerId, Long channelId, String channelOrderId, Status status) {
        this.clientId = clientId;
        this.customerId = customerId;
        this.channelId = channelId;
        this.channelOrderId = channelOrderId;
        this.status = status;
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

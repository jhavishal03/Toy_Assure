package com.increff.Dto;

import java.util.List;

public class OrderChannelRequestDto {
    private String channelName;
    private Long clientId;
    private Long customerId;
    private String channelOrderId;
    private List<OrderItemCsvDto> orderItems;

    public OrderChannelRequestDto() {
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
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

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public void setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
    }

    public List<OrderItemCsvDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemCsvDto> orderItems) {
        this.orderItems = orderItems;
    }
}

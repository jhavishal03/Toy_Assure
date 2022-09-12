package com.increff.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderChannelRequestForm {
    @NotEmpty
    private String channelName;
    @NotNull
    @Min(0)
    private Long clientId;
    @NotNull
    private Long customerId;
    @NotEmpty
    private String channelOrderId;
    @NotNull
    private List<OrderItemCsvForm> orderItems;
    
    
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
    
    public List<OrderItemCsvForm> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItemCsvForm> orderItems) {
        this.orderItems = orderItems;
    }
}

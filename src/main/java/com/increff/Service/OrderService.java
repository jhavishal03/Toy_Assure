package com.increff.Service;

import com.increff.Model.OrderAllocatedData;
import com.increff.Model.OrderChannelRequestDto;
import com.increff.Model.OrderItemCsvDto;
import com.increff.Pojo.OrderItem;

import java.net.URISyntaxException;
import java.util.List;

public interface OrderService {
    
    public List<OrderItem> createOrderInternalChannel(String customerName, String clientName, String channelOrderId, List<OrderItemCsvDto> orders);
    
    
    public List<OrderItem> createOrderExternalChannel(OrderChannelRequestDto orderRequest);
    
    public OrderAllocatedData allocateOrderPerId(Long orderId);
    
    public void generateFulfilledInvoice(Long orderId) throws URISyntaxException;
    
    public List<OrderItem> getOrderDetailsByOrderId(Long orderId);
}

package com.increff.Service;

import com.increff.Exception.ApiGenericException;
import com.increff.Model.OrderAllocatedData;
import com.increff.Model.OrderChannelRequestForm;
import com.increff.Model.OrderItemCsvForm;
import com.increff.Pojo.OrderItem;

import java.net.URISyntaxException;
import java.util.List;

public interface OrderService {
    
    public List<OrderItem> createOrderInternalChannel(String customerName, String clientName, String channelOrderId, List<OrderItemCsvForm> orders) throws ApiGenericException;
    
    
    public List<OrderItem> createOrderExternalChannel(OrderChannelRequestForm orderRequest) throws ApiGenericException;
    
    public OrderAllocatedData allocateOrderPerId(Long orderId);
    
    public void generateFulfilledInvoice(Long orderId) throws URISyntaxException;
    
    public List<OrderItem> getOrderDetailsByOrderId(Long orderId);
}

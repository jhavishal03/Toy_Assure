package com.increff.Dto.Converter;

import com.increff.Dto.OrderDto;
import com.increff.Dto.OrderItemCsvDto;
import com.increff.Model.Order;
import com.increff.Model.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderConverter {

    public Order convertOrderDtoToOrderEntity(OrderDto orderDto) {
        Order order = new Order();
        order.setClientId(orderDto.getClientId());
        order.setCustomerId(orderDto.getCustomerId());
        order.setChannelOrderId(orderDto.getChannelOrderId());
        order.setStatus(orderDto.getStatus());
        order.setChannelId(orderDto.getChannelId());
        return order;
    }

    public OrderItem convertOrderItemFromOrdenrItemCsvDto(Long globalSkuId, Long orderId, OrderItemCsvDto orderItemCsvDto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setGlobalSkuId(globalSkuId);
        orderItem.setOrderedQuantity(orderItemCsvDto.getOrderedQuantity());
        orderItem.setSellingPricePerUnit(orderItemCsvDto.getSellingPricePerUnit());
        
        return orderItem;
    }
}

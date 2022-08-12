package com.increff.Service;

import com.increff.Dto.OrderChannelRequestDto;
import com.increff.Model.OrderItem;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrderService {

    public List<OrderItem> createOrderInternalChannel(Long customerId, Long clientId, String channelOrderId, MultipartFile orderItems);


    public List<OrderItem> createOrderExternalChannel(OrderChannelRequestDto orderRequest);

    public List<OrderItem> allocateOrderPerId(Long orderId);
}

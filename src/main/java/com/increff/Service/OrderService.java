package com.increff.Service;

import com.increff.Model.OrderItem;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrderService {

    public List<OrderItem> createOrderInternalChannel(Long customerId, Long clientId, String channelOrderId, MultipartFile orderItems);
}

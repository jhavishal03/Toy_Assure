package com.increff.controller;

import com.increff.Model.OrderItem;
import com.increff.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/customer/{customerId}/client/{clientId}/createOrder")
    public ResponseEntity<List<OrderItem>> createOrderInternalChannel(@PathVariable("customerId") Long customerId, @PathVariable("clientId") Long clientId,
                                                                      @RequestParam(required = true) String channelOrderId, @RequestBody MultipartFile orderItems) {
        List<OrderItem> res = orderService.createOrderInternalChannel(customerId, clientId, channelOrderId, orderItems);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}

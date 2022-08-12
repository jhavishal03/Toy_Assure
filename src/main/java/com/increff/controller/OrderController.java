package com.increff.controller;

import com.increff.Dto.OrderChannelRequestDto;
import com.increff.Model.OrderItem;
import com.increff.Service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Api
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "Api to create Order using internal channel")
    @PostMapping("/customer/{customerId}/client/{clientId}/createOrder")
    public ResponseEntity<List<OrderItem>> createOrderInternalChannel(@PathVariable("customerId") Long customerId, @PathVariable("clientId") Long clientId,
                                                                      @RequestParam(required = true) String channelOrderId, @RequestBody MultipartFile orderItems) {
        List<OrderItem> res = orderService.createOrderInternalChannel(customerId, clientId, channelOrderId, orderItems);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Api to create order using external channel")
    @PostMapping("/order/channel/create")
    public void createOrderChannel(@RequestBody OrderChannelRequestDto orderRequest) {
        orderService.createOrderExternalChannel(orderRequest);
    }

    @ApiOperation(value = "Api to allocate Order by passing order Id")
    @PostMapping("/order/{orderId}/allocateOrders")
    public ResponseEntity<List<OrderItem>> allocateOrder(@PathVariable("orderId") Long orderId) {
        List<OrderItem> res = orderService.allocateOrderPerId(orderId);
        return new ResponseEntity<>(res, HttpStatus.OK);

    }
}

package com.increff.controller;

import com.increff.Dto.OrderChannelRequestDto;
import com.increff.Dto.Response;
import com.increff.Model.OrderItem;
import com.increff.Service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@Api
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;
    
    @ApiOperation(value = "Api to create Order using internal channel")
    @PostMapping("/customer/{customerId}/client/{clientId}/createOrder")
    public ResponseEntity<Response> createOrderInternalChannel(@PathVariable("customerId") Long customerId, @PathVariable("clientId") Long clientId,
                                                               @RequestParam(required = true) String channelOrderId, @RequestBody MultipartFile orderItems) {
        List<OrderItem> res = orderService.createOrderInternalChannel(customerId, clientId, channelOrderId, orderItems);
        Response response = new Response("Internal Order Created", res);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @ApiOperation(value = "Api to create order using external channel")
    @PostMapping("/order/channel/create")
    public ResponseEntity<Response> createOrderChannel(@RequestBody OrderChannelRequestDto orderRequest) {
        List<OrderItem> res = orderService.createOrderExternalChannel(orderRequest);
        return new ResponseEntity<>(new Response("external order created", res), HttpStatus.OK);
    }
    
    @ApiOperation(value = "Api to allocate Order by passing order Id")
    @PostMapping("/order/{orderId}/allocateOrders")
    public ResponseEntity<Response> allocateOrder(@PathVariable("orderId") Long orderId) {
        List<OrderItem> res = orderService.allocateOrderPerId(orderId);
        Response response = new Response("Order allocated", res);
        return new ResponseEntity<>(response, HttpStatus.OK);
        
    }
    
    @GetMapping("/order/{orderId}/generateInvoice")
    public ResponseEntity<Response> generateInvoice(@PathVariable("orderId") Long orderId) throws URISyntaxException {
        orderService.generateFulfilledInvoice(orderId);
        return new ResponseEntity<>(new Response<>(), HttpStatus.CREATED);
    }
}

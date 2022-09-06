package com.increff.controller;

import com.increff.Dto.OrderDto;
import com.increff.Model.OrderChannelRequestDto;
import com.increff.Model.Response;
import com.increff.Pojo.OrderItem;
import com.increff.Service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@Api
@RequestMapping("/api")
@Validated
public class OrderController {
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderDto orderDto;
    
    @ApiOperation(value = "Api to create Order using internal channel")
    @PostMapping("/customer/{customerName}/client/{clientName}/createOrder")
    public ResponseEntity<Response> createOrderInternalChannel(@PathVariable("customerName") String customerName, @PathVariable("clientName") String clientName,
                                                               @RequestParam(required = true) String channelOrderId, @RequestBody MultipartFile orderItems) {
        List<OrderItem> res = orderDto.createOrderInternalChannel(customerName, clientName, channelOrderId, orderItems);
        Response response = new Response("Internal Order Created", res);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @ApiOperation(value = "Api to create order using external channel")
    @PostMapping("/order/channel/createOrder")
    public ResponseEntity<Response> createOrderChannel(@RequestBody @Valid OrderChannelRequestDto orderRequest) {
        List<OrderItem> res = orderDto.createOrderExternalChannel(orderRequest);
        return new ResponseEntity<>(new Response("external order created", res), HttpStatus.OK);
    }
    
    @ApiOperation(value = "Api to allocate Order by passing order Id")
    @PostMapping("/order/{orderId}/allocateOrders")
    public ResponseEntity<Response> allocateOrder(@PathVariable("orderId") @Min(0) Long orderId) {
        List<OrderItem> res = orderDto.allocateOrderPerId(orderId);
        Response response = new Response("Order allocated", res);
        return new ResponseEntity<>(response, HttpStatus.OK);
        
    }
    
    @GetMapping("/order/{orderId}/fulfillOrder")
    public ResponseEntity<Response> generateInvoice(@PathVariable("orderId") @Min(0) Long orderId) throws URISyntaxException {
        orderService.generateFulfilledInvoice(orderId);
        return new ResponseEntity<>(new Response<>("Pdf created", ""), HttpStatus.CREATED);
    }
    
    @GetMapping("/order/{orderId}/getOrderDetails")
    public ResponseEntity<Response> getOrderDetailsByOrderId(@PathVariable("orderId") @Min(0) Long orderId) {
        List<OrderItem> res = orderDto.getOrderDetailsByOrderId(orderId);
        
        return new ResponseEntity<>(new Response("Order Item By Order Id", res), HttpStatus.OK);
    }
}

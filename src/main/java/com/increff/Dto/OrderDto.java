package com.increff.Dto;

import com.increff.Exception.ApiGenericException;
import com.increff.Exception.CSVFileParsingException;
import com.increff.Model.OrderChannelRequestDto;
import com.increff.Model.OrderItemCsvDto;
import com.increff.Pojo.OrderItem;
import com.increff.Service.OrderService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderDto {
    @Autowired
    private OrderService orderService;
    
    public List<OrderItem> createOrderInternalChannel(String customerName, String clientName, String channelOrderId, MultipartFile orderItems) {
        List<OrderItemCsvDto> orders = null;
        try {
            orders = new CsvToBeanBuilder(new InputStreamReader(new ByteArrayInputStream(orderItems.getBytes())))
                    .withType(OrderItemCsvDto.class).withSkipLines(1).build().parse();
        } catch (Exception e) {
            throw new CSVFileParsingException(e.getCause() + e.getMessage());
        }
        isOrderItemsApplicable(orders);
        return orderService.createOrderInternalChannel(customerName, clientName, channelOrderId, orders);
    }
    
    public List<OrderItem> createOrderExternalChannel(OrderChannelRequestDto orderRequest) {
        this.isOrderItemsApplicable(orderRequest.getOrderItems());
        return orderService.createOrderExternalChannel(orderRequest);
    }
    
    public List<OrderItem> allocateOrderPerId(Long orderId) {
        return orderService.allocateOrderPerId(orderId);
    }
    
    public List<OrderItem> getOrderDetailsByOrderId(Long orderId) {
        return orderService.getOrderDetailsByOrderId(orderId);
    }
    
    private void isOrderItemsApplicable(List<OrderItemCsvDto> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            throw new ApiGenericException("Order Items should not be empty ");
        }
        Set<String> skuIds = new HashSet<>();
        //can be used in util class
        Set<String> duplicateIds = orders.stream().map(ord -> ord.getClientSkuId())
                .filter(ele -> !skuIds.add(ele)).collect(Collectors.toSet());
        if (duplicateIds.size() != 0) {
            throw new ApiGenericException("Duplicate Sku present in CSV file with sku-> " + duplicateIds);
            
        }
    }
}

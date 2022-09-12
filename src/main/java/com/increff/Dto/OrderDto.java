package com.increff.Dto;

import com.increff.Exception.ApiGenericException;
import com.increff.Model.OrderAllocatedData;
import com.increff.Model.OrderChannelRequestForm;
import com.increff.Model.OrderItemCsvForm;
import com.increff.Pojo.OrderItem;
import com.increff.Service.OrderService;
import com.increff.Util.CSVParseUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderDto {
    @Autowired
    private OrderService orderService;
    
    public List<OrderItem> createOrderInternalChannel(String customerName, String clientName, String channelOrderId, MultipartFile orderItems) {
        List<OrderItemCsvForm> orders = null;
        try {
            orders = CSVParseUtil.parseCSV(orderItems.getBytes(), OrderItemCsvForm.class);
//                    new CsvToBeanBuilder(new InputStreamReader(new ByteArrayInputStream(orderItems.getBytes())))
//                    .withType(OrderItemCsvDto.class).withSkipLines(1).build().parse();
        } catch (IOException e) {
            throw new ApiGenericException("CSV IO exception while reading");
        }
        isOrderItemsApplicable(orders);
        return orderService.createOrderInternalChannel(customerName.trim().toLowerCase(),
                clientName.trim().toLowerCase(), channelOrderId.trim().toLowerCase(), orders);
    }
    
    public List<OrderItem> createOrderExternalChannel(OrderChannelRequestForm orderRequest) {
        this.isOrderItemsApplicable(orderRequest.getOrderItems());
        
        return orderService.createOrderExternalChannel(normalizeData(orderRequest));
    }
    
    private OrderChannelRequestForm normalizeData(OrderChannelRequestForm orderRequest) {
        orderRequest.setChannelOrderId(orderRequest.getChannelOrderId().trim().toLowerCase());
        orderRequest.setChannelName(orderRequest.getChannelName().trim().toLowerCase());
        return orderRequest;
    }
    
    public OrderAllocatedData allocateOrderPerId(Long orderId) {
        return orderService.allocateOrderPerId(orderId);
    }
    
    public List<OrderItem> getOrderDetailsByOrderId(Long orderId) {
        return orderService.getOrderDetailsByOrderId(orderId);
    }
    
    private void isOrderItemsApplicable(List<OrderItemCsvForm> orders) {
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

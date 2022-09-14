package com.increff.Dto;

import com.increff.Constants.InvoiceType;
import com.increff.Constants.Status;
import com.increff.Constants.UserType;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.Helper.DtoHelper;
import com.increff.Model.OrderAllocatedData;
import com.increff.Model.OrderItemCsvForm;
import com.increff.Pojo.*;
import com.increff.Service.*;
import com.increff.Util.CSVParseUtil;
import com.increff.Util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderDto {
    
    private OrderApi orderApi;
    
    private UserApi userApi;
    
    private BinApi binApi;
    
    private InventoryApi inventoryApi;
    @Autowired
    private InvoiceApi invoiceApi;
    @Autowired
    private ChannelApi channelApi;
    
    @Autowired
    public OrderDto(OrderApi orderApi, UserApi userApi, BinApi binApi, InventoryApi inventoryApi) {
        this.orderApi = orderApi;
        this.userApi = userApi;
        this.binApi = binApi;
        this.inventoryApi = inventoryApi;
    }
    
    public List<OrderItemPojo> createOrderInternalChannel(String customerName, String clientName,
                                                          String channelOrderId, MultipartFile orderItems) {
        
        StringUtil.toLowerCase(customerName);
        StringUtil.toLowerCase(clientName);
        StringUtil.toLowerCase(channelOrderId);
        List<OrderItemCsvForm> orders = parseCsvOrder(orderItems);
        Optional<UserPojo> customer = userApi.getUserByNameAndType(customerName, UserType.CUSTOMER);
        //checking client exist or not
        Optional<UserPojo> client = userApi.getUserByNameAndType(clientName, UserType.CLIENT);
        if (orderApi.isChannelOrderDuplicate(channelOrderId)) {
            throw new ApiGenericException("channel OrderId duplicate");
        }
        ChannelPojo channelPojo = channelApi.getChannelByNameAndType("INTERNAL", InvoiceType.SELF);
        
        return orderApi.upsertOrder(customer.get().getUserId(), client.get().getUserId(),
                channelOrderId, channelPojo.getChannelId(), orders);
        
    }
    
    
    @Transactional
    public OrderAllocatedData allocateOrderPerId(Long orderId) {
        List<OrderItemPojo> res = new ArrayList<>();
        DtoHelper.check(orderId, " orderId ");
        OrderPojo order = orderApi.getOrderPojoById(orderId);
        DtoHelper.check(order);
        List<OrderItemPojo> orderItems = orderApi.getOrderDetailsByOrderId(orderId);
        boolean isOrderAllocated = true;
        for (OrderItemPojo orderItemPojo : orderItems) {
            InventoryPojo inventoryPojo = inventoryApi.getInventoryByGlobalSkuId(orderItemPojo.getGlobalSkuId());
            
            Long allocatedItem = Math.min((orderItemPojo.getOrderedQuantity() - orderItemPojo.getAllocatedQuantity())
                    , inventoryPojo.getAvailableQuantity());
            
            orderItemPojo.setAllocatedQuantity(orderItemPojo.getAllocatedQuantity() + allocatedItem);
            
            if (Long.compare(orderItemPojo.getOrderedQuantity(), orderItemPojo.getAllocatedQuantity()) != 0) {
                isOrderAllocated = false;
            }
            inventoryPojo.setAvailableQuantity(inventoryPojo.getAvailableQuantity() - allocatedItem);
            inventoryPojo.setAllocatedQuantity(inventoryPojo.getAllocatedQuantity() + allocatedItem);
            binApi.removeProductsFromBinAfterAllocation(orderItemPojo.getGlobalSkuId(), allocatedItem);
            res.add(orderItemPojo);
        }
        if (isOrderAllocated == true) {
            order.setStatus(Status.ALLOCATED);
            return new OrderAllocatedData(true, res);
        }
        return new OrderAllocatedData(false, res);
        
    }
    
    public void fulfillOrder(Long orderId) throws URISyntaxException {
        DtoHelper.check(orderId, " OrderId");
        OrderPojo orderPojo = orderApi.getOrderPojoById(orderId);
        DtoHelper.checkFulfil(orderPojo);
        List<OrderItemPojo> orderItemPojoList = orderApi.getOrderDetailsByOrderId(orderId);
        invoiceApi.generateInvoice(orderItemPojoList, orderPojo);
        orderPojo.setStatus(Status.FULFILLED);
        orderApi.updateOrderPojo(orderPojo);
    }
    
    public List<OrderItemPojo> getOrderDetailsByOrderId(Long orderId) {
        return orderApi.getOrderDetailsByOrderId(orderId);
    }
    
    private List<OrderItemCsvForm> parseCsvOrder(MultipartFile orderItems) {
        List<OrderItemCsvForm> orders = null;
        try {
            orders = CSVParseUtil.parseCSV(orderItems.getBytes(), OrderItemCsvForm.class);
        } catch (IOException e) {
            throw new ApiGenericException("CSV IO exception while reading");
        }
        isOrderItemsApplicable(orders);
        return orders;
    }
    
    private void isOrderItemsApplicable(List<OrderItemCsvForm> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            throw new ApiGenericException("OrderPojo Items should not be empty ");
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

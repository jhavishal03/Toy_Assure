package com.increff.Dto;

import com.increff.Constants.InvoiceType;
import com.increff.Constants.Status;
import com.increff.Constants.UserType;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.Helper.DtoHelper;
import com.increff.Model.OrderAllocatedData;
import com.increff.Model.OrderChannelRequestForm;
import com.increff.Model.OrderItemCsvForm;
import com.increff.Model.OrderItemForm;
import com.increff.Pojo.ChannelPojo;
import com.increff.Pojo.OrderItemPojo;
import com.increff.Pojo.OrderPojo;
import com.increff.Pojo.UserPojo;
import com.increff.Service.*;
import com.increff.Service.Flow.OrderFlowApi;
import com.increff.Util.CSVParseUtil;
import com.increff.Util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private OrderFlowApi orderFlowApi;
    
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
        
        return orderFlowApi.upsertOrderInternal(customer.get().getUserId(), client.get().getUserId(),
                channelOrderId, channelPojo.getChannelId(), orders);
        
    }
    
    public List<OrderItemPojo> createOrderExternalChannel(OrderChannelRequestForm orderRequest) {
        StringUtil.toLowerCase(orderRequest.getChannelName());
        StringUtil.toLowerCase(orderRequest.getChannelOrderId());
        List<OrderItemForm> orderItems = orderRequest.getOrderItems();
        UserPojo customer = userApi.findUserById(orderRequest.getCustomerId(), UserType.CUSTOMER);
        //checking client exist or not
        UserPojo client = userApi.findUserById(orderRequest.getClientId(), UserType.CLIENT);
        if (orderApi.isChannelOrderDuplicate(orderRequest.getChannelOrderId())) {
            throw new ApiGenericException("channel OrderId duplicate");
        }
        ChannelPojo channelPojo = channelApi.getChannelByNameAndType(orderRequest.getChannelName(), InvoiceType.CHANNEL);
        
        return orderFlowApi.upsertOrderExternal(customer.getUserId(), client.getUserId(),
                orderRequest.getChannelOrderId(), channelPojo.getChannelId(), orderItems);
        
    }
    
    
    public OrderAllocatedData allocateOrderPerId(Long orderId) {
        DtoHelper.check(orderId, " orderId ");
        return orderFlowApi.allocateOrderById(orderId);
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

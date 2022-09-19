package com.increff.Service.Flow;

import com.increff.Constants.Status;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.Helper.DtoHelper;
import com.increff.Model.OrderAllocatedData;
import com.increff.Model.OrderItemCsvForm;
import com.increff.Model.OrderItemForm;
import com.increff.Pojo.InventoryPojo;
import com.increff.Pojo.OrderItemPojo;
import com.increff.Pojo.OrderPojo;
import com.increff.Pojo.ProductPojo;
import com.increff.Service.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class OrderFlowApi {
    private static Logger logger = Logger.getLogger(OrderApi.class.getName());
    
    private ChannelApi channelApi;
    private OrderApi orderApi;
    private ProductApi productApi;
    
    private BinApi binApi;
    private InventoryApi inventoryApi;
    
    @Autowired
    public OrderFlowApi(ChannelApi channelApi, OrderApi orderApi, ProductApi productApi,
                        BinApi binApi, InventoryApi inventoryApi) {
        this.channelApi = channelApi;
        this.orderApi = orderApi;
        this.productApi = productApi;
        this.binApi = binApi;
        this.inventoryApi = inventoryApi;
    }
    
    @Transactional
    public List<OrderItemPojo> upsertOrderExternal(Long customerId, Long clientId, String channelOrderId, Long channelId, List<OrderItemForm> orders) {
        
        OrderPojo orderPojo = OrderPojo.builder().customerId(customerId).clientId(clientId)
                .channelId(channelId).channelOrderId(channelOrderId).status(Status.CREATED).build();
        
        OrderPojo savedOrder = orderApi.addOrderPojo(orderPojo);
        return this.upsertOrderItemDetailsExternal(clientId, savedOrder.getOrderId(), channelId,
                orders);
    }
    
    @Transactional
    public List<OrderItemPojo> upsertOrderInternal(Long customerId, Long clientId, String channelOrderId, Long channelId, List<OrderItemCsvForm> orders) {
        
        OrderPojo orderPojo = OrderPojo.builder().customerId(customerId).clientId(clientId)
                .channelId(channelId).channelOrderId(channelOrderId).status(Status.CREATED).build();
        
        OrderPojo savedOrder = orderApi.addOrderPojo(orderPojo);
        return this.upsertOrderItemDetailsInternal(clientId, savedOrder.getOrderId(),
                orders);
    }
    
    @Transactional
    public OrderAllocatedData allocateOrderById(Long orderId) {
        List<OrderItemPojo> res = new ArrayList<>();
        OrderPojo order = orderApi.getOrderPojoById(orderId);
        DtoHelper.check(order);
        List<OrderItemPojo> orderItems = orderApi.getOrderDetailsByOrderId(orderId);
        boolean isOrderAllocated = true;
        for (OrderItemPojo orderItemPojo : orderItems) {
            InventoryPojo inventoryPojo = inventoryApi.getInventoryByGlobalSkuId(orderItemPojo.getGlobalSkuId());
            Long allocatedItem = updateInventoryDataAfterOrderAllocation(orderItemPojo, inventoryPojo);
            binApi.removeProductsFromBinAfterAllocation(orderItemPojo.getGlobalSkuId(), allocatedItem);
            if (orderItemPojo.getOrderedQuantity() != orderItemPojo.getAllocatedQuantity()) {
                isOrderAllocated = false;
            }
            res.add(orderItemPojo);
        }
        if (isOrderAllocated == true) {
            order.setStatus(Status.ALLOCATED);
            return new OrderAllocatedData(true, res);
        }
        return new OrderAllocatedData(false, res);
        
    }
    
    private Long updateInventoryDataAfterOrderAllocation(OrderItemPojo orderItemPojo, InventoryPojo inventoryPojo) {
        Long allocatedItem = Math.min((orderItemPojo.getOrderedQuantity() - orderItemPojo.getAllocatedQuantity())
                , inventoryPojo.getAvailableQuantity());
        orderItemPojo.setAllocatedQuantity(orderItemPojo.getAllocatedQuantity() + allocatedItem);
        inventoryPojo.setAvailableQuantity(inventoryPojo.getAvailableQuantity() - allocatedItem);
        inventoryPojo.setAllocatedQuantity(inventoryPojo.getAllocatedQuantity() + allocatedItem);
        return allocatedItem;
    }
    
    private List<OrderItemPojo> upsertOrderItemDetailsInternal(Long clientId, Long orderId, List<OrderItemCsvForm> orders) {
        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        //for each product
        for (OrderItemCsvForm order : orders) {
            ProductPojo savedProductPojo = productApi.findProductByClientIdAndSkuId(clientId, order.getClientSkuId());
            if (savedProductPojo == null) {
                throw new ApiGenericException("ProductPojo not exists for order with SKuID", order.getClientSkuId());
            }
            orderItemPojoList.add(OrderItemPojo.builder().orderId(orderId).globalSkuId(savedProductPojo.getGlobalSkuId()).
                    orderedQuantity(order.getOrderedQuantity()).sellingPricePerUnit(order.getSellingPricePerUnit())
                    .allocatedQuantity(0l).fulfilledQuantity(0l).build());
            
        }
        if (CollectionUtils.isEmpty(orderItemPojoList)) {
            throw new ApiGenericException("All the provided SkuIds not present");
        }
        orderApi.addOrderItemList(orderItemPojoList);
        return orderItemPojoList;
    }
    
    private List<OrderItemPojo> upsertOrderItemDetailsExternal(Long clientId, Long orderId, Long channelId, List<OrderItemForm> orderItems) {
        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        List<String> skuIdsNotPresent = new ArrayList<>();
        for (OrderItemForm order : orderItems) {
            Long globalSkuId = channelApi.getGlobalSkuIDByClientIdAndChannelIdAndSkuId(clientId, channelId, order.getChannelSkuId());
            if (globalSkuId == null) {
                skuIdsNotPresent.add(order.getChannelSkuId());
            } else {
                Double price = productApi.findMrpByGlobalSkuId(globalSkuId);
                orderItemPojoList.add(OrderItemPojo.builder().orderId(orderId).globalSkuId(globalSkuId).
                        orderedQuantity(order.getOrderedQuantity()).sellingPricePerUnit(price)
                        .allocatedQuantity(0l).fulfilledQuantity(0l).build());
            }
        }
        if (CollectionUtils.isEmpty(orderItemPojoList)) {
            throw new ApiGenericException("All the provided SkuIds not present or channel Listing is not provided");
        }
        if (skuIdsNotPresent.size() != 0) {
            logger.info("some of the Sku ids are not exist or channel listing not provided");
            throw new ApiGenericException("These Sku Ids Donot have channelListings for client", skuIdsNotPresent);
        }
        return orderApi.addOrderItemList(orderItemPojoList);
        
    }
    
    
}

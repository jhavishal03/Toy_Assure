package com.increff.Service;

import com.increff.Constants.Status;
import com.increff.Constants.UserType;
import com.increff.Dao.OrderDao;
import com.increff.Dao.OrderItemDao;
import com.increff.Dao.ProductDao;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.OrderItemCsvForm;
import com.increff.Pojo.OrderItemPojo;
import com.increff.Pojo.OrderPojo;
import com.increff.Pojo.ProductPojo;
import com.increff.Pojo.UserPojo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class OrderApi {
    
    private static Logger logger = Logger.getLogger(OrderApi.class.getName());
    
    
    private UserApi userApi;
    private ChannelApi channelApi;
    private OrderDao orderDao;
    private OrderItemDao orderItemDao;
    private ProductDao productDao;
    
    @Autowired
    public OrderApi(UserApi userApi, ChannelApi channelApi, OrderDao orderDao, OrderItemDao orderItemDao, ProductDao productDao) {
        this.userApi = userApi;
        this.channelApi = channelApi;
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.productDao = productDao;
    }
    
    //    @Transactional(rollbackFor = ApiGenericException.class)
//    public List<OrderItemPojo> createOrderInternalChannel(String customerName, String clientName, String channelOrderId, List<OrderItemCsvForm> orders) throws ApiGenericException {
//        //checking whether customer exist or not
//        List<OrderItemPojo> result = null;
//        Optional<UserPojo> customer = userApi.getUserByNameAndType(customerName, UserType.CUSTOMER);
//        if (!customer.isPresent() || !customer.get().getType().getValue().equals("Customer")) {
//            throw new ApiGenericException("Customer not present with name " + customerName);
//        }
//        //checking client exist or not
//        Optional<UserPojo> client = userApi.getUserByNameAndType(clientName, UserType.CLIENT);
//        if (!client.isPresent() || !client.get().getType().equals(UserType.CLIENT)) {
//            throw new ApiGenericException("Client  not present with name" + clientName);
//        }
//        if (isChannelOrderDuplicate(channelOrderId)) {
//            throw new ApiGenericException("channel OrderId duplicate");
//        }
//        OrderPojo orderPojo = orderDao.addOrder(upsertOrder(customer.get().getUserId(), client.get().getUserId(), channelOrderId, "INTERNAL", InvoiceType.SELF));
//        result = upsertOrderItemDetailsInternal(client.get().getUserId(), orderPojo.getOrderId(), orders);
//        return result;
//    }
    public OrderPojo addOrderPojo(OrderPojo orderPojo) {
        return orderDao.addOrder(orderPojo);
    }
    
    @Transactional
    public OrderPojo updateOrderPojo(OrderPojo orderPojo) {
        return orderDao.updateOrder(orderPojo);
    }
    
    //    @Transactional(isolation = Isolation.SERIALIZABLE)
//    public List<OrderItemPojo> createOrderExternalChannel(OrderChannelRequestForm orderRequest) {
//        List<OrderItemPojo> result = null;
//        //repetitive code  can be used in a method
//        Optional<UserPojo> customer = Optional.ofNullable(userApi.findUserById(orderRequest.getCustomerId()));
//        if (!customer.isPresent()) {
//            throw new ApiGenericException("Customer not present with id " + orderRequest.getCustomerId());
//        }
//        //checking client exist or not
//        Optional<UserPojo> client = Optional.ofNullable(userApi.findUserById(orderRequest.getClientId()));
//        if (!client.isPresent() || !client.get().getType().equals(UserType.CLIENT)) {
//            throw new ApiGenericException("Client  not present with id " + orderRequest.getClientId());
//        }
//
//        ChannelPojo channelPojo = channelApi.getChannelByChannelName(orderRequest.getChannelName());
//
//        if (isChannelOrderDuplicate(orderRequest.getChannelOrderId())) {
//            throw new ApiGenericException("channelPojo OrderId duplicate");
//        }
//        logger.info("OrderPojo adding");
//        OrderPojo orderPojo = orderDao.addOrder(upsertOrder(orderRequest.getCustomerId(), orderRequest.getClientId(),
//                orderRequest.getChannelOrderId(), orderRequest.getChannelName(), InvoiceType.CHANNEL));
//        logger.info("orderPojo added ->" + orderPojo.toString());
//        result = upsertOrderItemDetailsExternal(orderRequest.getClientId(), orderPojo.getOrderId(),
//                channelPojo.getChannelId(), orderRequest.getOrderItems());
//
//        if (CollectionUtils.isEmpty(result)) {
//            throw new ApiGenericException("OrderPojo not created as All the provided SkuIds not present ");
//        }
//        return result;
//    }
//
    public OrderPojo getOrderPojoById(Long orderId) {
        OrderPojo orderPojo = orderDao.findOrderByOrderId(orderId);
        if (orderPojo == null) {
            throw new ApiGenericException("Order Entity not exist");
        }
        return orderPojo;
    }

//    public OrderAllocatedData allocateOrderPerId(Long orderId) {
//        OrderPojo orderPojo = orderDao.findOrderByOrderId(orderId);
//        List<OrderItemPojo> res = new ArrayList<>();
//        if (orderPojo == null) {
//            throw new ApiGenericException("OrderPojo not exist");
//        }
//        if (orderPojo.getStatus().equals(Status.ALLOCATED)) {
//            //orderPojo already allocated
//            throw new ApiGenericException("OrderPojo already Allocated");
//        }
//
//        if (orderPojo.getStatus().equals(Status.FULFILLED)) {
//            //orderPojo already allocated
//            throw new ApiGenericException("OrderPojo already Fulfilled");
//        }
//        List<OrderItemPojo> orderItemPojos = orderItemDao.fetchOrderItemByOrderId(orderId);
//
//        boolean isOrderAllocated = true;
//        for (OrderItemPojo orderItemPojo : orderItemPojos) {
//            InventoryPojo inventoryPojo = inventoryService.getInventoryByGlobalSkuId(orderItemPojo.getGlobalSkuId());
//
//            Long allocatedItem = Math.min((orderItemPojo.getOrderedQuantity() - orderItemPojo.getAllocatedQuantity())
//                    , inventoryPojo.getAvailableQuantity());
//
//            orderItemPojo.setAllocatedQuantity(orderItemPojo.getAllocatedQuantity() + allocatedItem);
//
//            if (Long.compare(orderItemPojo.getOrderedQuantity(), orderItemPojo.getAllocatedQuantity()) != 0) {
//                isOrderAllocated = false;
//            }
//            inventoryPojo.setAvailableQuantity(inventoryPojo.getAvailableQuantity() - allocatedItem);
//            inventoryPojo.setAllocatedQuantity(inventoryPojo.getAllocatedQuantity() + allocatedItem);
//            binService.removeProductsFromBinAfterAllocation(orderItemPojo.getGlobalSkuId(), allocatedItem);
//            res.add(orderItemPojo);
//        }
//        if (isOrderAllocated == true) {
//            orderPojo.setStatus(Status.ALLOCATED);
////            orderDao.addOrder(orderPojo);
//            return new OrderAllocatedData(true, res);
//        }
//        return new OrderAllocatedData(false, res);
//
//    }
//
    
    
    public List<OrderItemPojo> getOrderDetailsByOrderId(Long orderId) {
        logger.info("getOrderDetailsBy Id started");
        OrderPojo orderPojo = this.getOrderPojoById(orderId);
        
        List<OrderItemPojo> orderItemPojoList = orderItemDao.fetchOrderItemByOrderId(orderId);
        logger.info("OrderPojo details are" + orderItemPojoList);
        logger.info("getOrderDetailsBy Id End");
        return orderItemPojoList;
    }
    
    
    private List<OrderItemPojo> upsertOrderItemDetailsExternal(Long clientId, Long orderId, Long channelId, List<OrderItemCsvForm> orderItems) {
        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        List<String> skuIdsNotPresent = new ArrayList<>();
        for (OrderItemCsvForm order : orderItems) {
            Long globalSkuId = channelApi.getGlobalSkuIDByClientIdAndChannelIdAndSkuId(clientId, channelId, order.getClientSkuId());
            if (globalSkuId == null) {
                skuIdsNotPresent.add(order.getClientSkuId());
            } else {
//                Double price = productDao.findMrpByGlobalSkuID(globalSkuId);
                order.setSellingPricePerUnit(order.getSellingPricePerUnit());
                orderItemPojoList.add(OrderItemPojo.builder().orderId(orderId).globalSkuId(globalSkuId).
                        orderedQuantity(order.getOrderedQuantity()).sellingPricePerUnit(order.getSellingPricePerUnit())
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
        logger.info("idhar nahi aaya");
        return orderItemDao.addOrderItems(orderItemPojoList);
        
    }
    
    @Transactional
    public List<OrderItemPojo> upsertOrder(Long customerId, Long clientId, String channelOrderId, Long channelId, List<OrderItemCsvForm> orders) {
        
        OrderPojo orderPojo = OrderPojo.builder().customerId(customerId).clientId(clientId)
                .channelId(channelId).channelOrderId(channelOrderId).status(Status.CREATED).build();
        
        OrderPojo savedOrder = orderDao.addOrder(orderPojo);
        return this.upsertOrderItemDetailsInternal(clientId, savedOrder.getOrderId(),
                orders);
    }
    
    private List<OrderItemPojo> upsertOrderItemDetailsInternal(Long clientId, Long orderId, List<OrderItemCsvForm> orders) {
        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        //for each product
        for (OrderItemCsvForm order : orders) {
            ProductPojo savedProductPojo = productDao.getProductByClientIdAndClientSkuId(clientId, order.getClientSkuId());
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
        orderItemDao.addOrderItems(orderItemPojoList);
        return orderItemPojoList;
    }
    
    
    public boolean isChannelOrderDuplicate(String channelOrderId) {
        
        Long isChannelOrderIdExist = orderDao.checkChannelOrderIdExist(channelOrderId);
        
        return isChannelOrderIdExist == 1l ? true : false;
    }
    
    private void checkCustomerAndClientValidation(String customerName, String clientName) {
        Optional<UserPojo> customer = userApi.getUserByNameAndType(customerName, UserType.CUSTOMER);
        if (!customer.isPresent() || !customer.get().getType().getValue().equals("Customer")) {
            throw new ApiGenericException("Customer not present with name " + customerName);
        }
        //checking client exist or not
        Optional<UserPojo> client = userApi.getUserByNameAndType(clientName, UserType.CLIENT);
        if (!client.isPresent() || !client.get().getType().equals(UserType.CLIENT)) {
            throw new ApiGenericException("Client  not present with name" + clientName);
        }
    }
    
}

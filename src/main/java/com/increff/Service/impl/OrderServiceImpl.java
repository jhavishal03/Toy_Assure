package com.increff.Service.impl;

import com.increff.Constants.InvoiceType;
import com.increff.Constants.Status;
import com.increff.Constants.UserType;
import com.increff.Dao.*;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.OrderChannelRequestDto;
import com.increff.Model.OrderItemCsvDto;
import com.increff.Pojo.*;
import com.increff.Service.BinService;
import com.increff.Service.OrderService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class OrderServiceImpl implements OrderService {
    
    private static Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());
    private UserDao userDao;
    private ChannelDao channelDao;
    private OrderDao orderDao;
    private ProductDao productDao;
    private OrderItemDao orderItemDao;
    private InventoryDao inventoryDao;
    private BinDao binDao;
    private BinService binService;
    @Autowired
    private InvoiceServiceImpl invoiceService;
    
    @Autowired
    public OrderServiceImpl(UserDao userDao, ChannelDao channelDao, OrderDao orderDao,
                            ProductDao productDao,
                            OrderItemDao orderItemDao, InventoryDao inventoryDao,
                            BinDao binDao, BinService binService) {
        this.userDao = userDao;
        this.channelDao = channelDao;
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.orderItemDao = orderItemDao;
        this.inventoryDao = inventoryDao;
        this.binDao = binDao;
        this.binService = binService;
    }
    
    
    @Transactional(rollbackOn = ApiGenericException.class)
    @Override
    public List<OrderItem> createOrderInternalChannel(String customerName, String clientName, String channelOrderId, List<OrderItemCsvDto> orders) {
        //checking whether customer exist or not
        List<OrderItem> result = null;
        Optional<User> customer = userDao.getUserByNameAndType(customerName, UserType.CUSTOMER);
        if (!customer.isPresent() || !customer.get().getType().getValue().equals("Customer")) {
            throw new ApiGenericException("Customer not present with name " + customerName);
        }
        //checking client exist or not
        Optional<User> client = userDao.getUserByNameAndType(clientName, UserType.CLIENT);
        if (!client.isPresent() || !client.get().getType().equals(UserType.CLIENT)) {
            throw new ApiGenericException("Client  not present with name" + clientName);
        }
        if (isChannelOrderDuplicate(channelOrderId)) {
            throw new ApiGenericException("channel OrderId duplicate");
        }
        //save order details
//        Order ord=upsertOrder(customerId, clientId, channelOrderId, "INTERNAL", InvoiceType.SELF);
        Order order = orderDao.addOrder(upsertOrder(customer.get().getUserId(), client.get().getUserId(), channelOrderId, "INTERNAL", InvoiceType.SELF));
        result = upsertOrderItemDetailsInternal(client.get().getUserId(), order.getOrderId(), orders);
        return result;
    }
    
    @Override
    @Transactional(rollbackOn = ApiGenericException.class)
    public List<OrderItem> createOrderExternalChannel(OrderChannelRequestDto orderRequest) {
        List<OrderItem> result = null;
        //repetitive code  can be used in a method
        Optional<User> customer = Optional.ofNullable(userDao.findUserById(orderRequest.getCustomerId()));
        if (!customer.isPresent()) {
            throw new ApiGenericException("Customer not present with id " + orderRequest.getCustomerId());
        }
        //checking client exist or not
        Optional<User> client = Optional.ofNullable(userDao.findUserById(orderRequest.getClientId()));
        if (!client.isPresent() || !client.get().getType().equals(UserType.CLIENT)) {
            throw new ApiGenericException("Client  not present with id " + orderRequest.getClientId());
        }
        
        Optional<Channel> channel = channelDao.checkChannelExistOrNot(orderRequest.getChannelName());
        if (!channel.isPresent()) {
            throw new ApiGenericException("channel nahi hai");
            
        }
        if (isChannelOrderDuplicate(orderRequest.getChannelOrderId())) {
            throw new ApiGenericException("channel OrderId duplicate");
        }
        Order order = orderDao.addOrder(upsertOrder(orderRequest.getCustomerId(), orderRequest.getClientId(),
                orderRequest.getChannelOrderId(), orderRequest.getChannelName(), InvoiceType.CHANNEL));
        result = upsertOrderItemDetailsExternal(orderRequest.getClientId(), order.getOrderId(),
                channel.get().getChannelId(), orderRequest.getOrderItems());
        if (CollectionUtils.isEmpty(result)) {
            throw new ApiGenericException("Order not created as All the provided SkuIds not present ");
        }
        return result;
    }
    
    private boolean isChannelOrderDuplicate(String channelOrderId) {
        
        Long isChannelOrderIdExist = orderDao.checkChannelOrderIdExist(channelOrderId);
        return isChannelOrderIdExist == 0l ? false : true;
    }
    
    private List<OrderItem> upsertOrderItemDetailsInternal(Long clientId, Long orderId, List<OrderItemCsvDto> orders) {
        List<OrderItem> orderItemList = new ArrayList<>();
        //for each product
        for (OrderItemCsvDto order : orders) {
            Product savedProduct = productDao.getProductForProductByClientIdAndClientSkuId(clientId, order.getClientSkuId());
            if (savedProduct != null) {
                orderItemList.add(OrderItem.builder().orderId(orderId).globalSkuId(savedProduct.getGlobalSkuId()).
                        orderedQuantity(order.getOrderedQuantity()).sellingPricePerUnit(order.getSellingPricePerUnit())
                        .allocatedQuantity(0l).fulfilledQuantity(0l).build());
            }
        }
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new ApiGenericException("All the provided SkuIds not present");
        }
        orderItemDao.addOrderItems(orderItemList);
        return orderItemList;
    }
    
    
    private Order upsertOrder(Long customerId, Long clientId, String channelOrderId, String channelname, InvoiceType type) {
        Long channelId = channelDao.getChannelIdByNameAndType(channelname, type);
        if (channelId == null) {
            throw new ApiGenericException("channel Not exist");
        }
        Order order = Order.builder().customerId(customerId).clientId(clientId)
                .channelId(channelId).channelOrderId(channelOrderId).status(Status.CREATED).build();
        
        return order;
    }
    
    @Override
    @Transactional(rollbackOn = ApiGenericException.class)
    public List<OrderItem> allocateOrderPerId(Long orderId) {
        Order order = orderDao.findOrderByOrderId(orderId);
        List<OrderItem> res = new ArrayList<>();
        if (order == null) {
            throw new ApiGenericException("Order not exist");
        }
        if (order.getStatus().equals(Status.ALLOCATED)) {
            //order already allocated
            throw new ApiGenericException("Order already Allocated");
        }
        
        if (order.getStatus().equals(Status.FULFILLED)) {
            //order already allocated
            throw new ApiGenericException("Order already Fulfilled");
        }
        List<OrderItem> orderItems = orderItemDao.fetchOrderItemByOrderId(orderId);
        
        boolean isOrderAllocated = true;
        for (OrderItem orderItem : orderItems) {
            Inventory inventory = inventoryDao.getInvetoryBySkuId(orderItem.getGlobalSkuId());
            if (inventory == null) {
                throw new ApiGenericException("InventoryDetails  not exist for Global Sku Id" + orderItem.getGlobalSkuId());
            }
            Long allocatedItem = Math.min((orderItem.getOrderedQuantity() - orderItem.getAllocatedQuantity())
                    , inventory.getAvailableQuantity());
            
            orderItem.setAllocatedQuantity(orderItem.getAllocatedQuantity() + allocatedItem);
            
            if (Long.compare(orderItem.getOrderedQuantity(), orderItem.getAllocatedQuantity()) != 0) {
                isOrderAllocated = false;
            }
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - allocatedItem);
            inventory.setAllocatedQuantity(inventory.getAllocatedQuantity() + allocatedItem);
            binService.removeProductsFromBinAfterAllocation(orderItem.getGlobalSkuId(), allocatedItem);
            res.add(orderItem);
        }
        if (isOrderAllocated == true) {
            order.setStatus(Status.ALLOCATED);
            orderDao.addOrder(order);
        } else {
            throw new ApiGenericException("Order Not Allocated" + res.toString());
        }
        
        return res;
    }
    
    @Override
    @Transactional(rollbackOn = ApiGenericException.class)
    public void generateFulfilledInvoice(Long orderId) throws URISyntaxException {
        Order order = orderDao.findOrderByOrderId(orderId);
        if (order == null) {
            throw new ApiGenericException("order doesn't exist or created");
        } else if (order.getStatus().getValue().equals(Status.FULFILLED.getValue())) {
            throw new ApiGenericException("Invoice already generated");
        } else if (order.getStatus().getValue().equals(Status.CREATED.getValue())) {
            throw new ApiGenericException("Order is not allocated Yet, So can't generate order Invoice");
        }
        List<OrderItem> orderItemList = orderItemDao.fetchOrderItemByOrderId(orderId);
        invoiceService.generateInvoice(orderItemList, order);
        order.setStatus(Status.FULFILLED);
    }
    
    @Override
    public List<OrderItem> getOrderDetailsByOrderId(Long orderId) {
        Order order = orderDao.findOrderByOrderId(orderId);
        if (order == null) {
            throw new ApiGenericException("order doesn't exist or created");
        }
        List<OrderItem> orderItemList = orderItemDao.fetchOrderItemByOrderId(orderId);
        return orderItemList;
    }
    
    private List<OrderItem> upsertOrderItemDetailsExternal(Long clientId, Long orderId, Long channelId, List<OrderItemCsvDto> orderItems) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (OrderItemCsvDto order : orderItems) {
            Long globalSkuId = channelDao.getGlobalSkuIDByChannelIdAndSkuId(clientId, channelId, order.getClientSkuId());
            Double price = productDao.findMrpByGlobalSkuID(globalSkuId);
            if (globalSkuId != null) {
                order.setSellingPricePerUnit(price);
                orderItemList.add(OrderItem.builder().orderId(orderId).globalSkuId(globalSkuId).
                        orderedQuantity(order.getOrderedQuantity()).sellingPricePerUnit(order.getSellingPricePerUnit())
                        .allocatedQuantity(0l).fulfilledQuantity(0l).build());
            }
        }
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new ApiGenericException("All the provided SkuIds not present or channel Listing is not provided");
        }
        if (Integer.compare(orderItems.size(), orderItemList.size()) != 0) {
            logger.info("some of the Sku ids are not exist or channel listing not provided");
        }
        return orderItemDao.addOrderItems(orderItemList);
        
    }
    
    private void checkCustomerAndclientValidation(String customerName, String clientName) {
        Optional<User> customer = userDao.getUserByNameAndType(customerName, UserType.CUSTOMER);
        if (!customer.isPresent() || !customer.get().getType().getValue().equals("Customer")) {
            throw new ApiGenericException("Customer not present with name " + customerName);
        }
        //checking client exist or not
        Optional<User> client = userDao.getUserByNameAndType(clientName, UserType.CLIENT);
        if (!client.isPresent() || !client.get().getType().equals(UserType.CLIENT)) {
            throw new ApiGenericException("Client  not present with name" + clientName);
        }
    }
    
}

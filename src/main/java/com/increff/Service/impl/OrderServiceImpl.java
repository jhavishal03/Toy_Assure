package com.increff.Service.impl;

import com.increff.Constants.InvoiceType;
import com.increff.Constants.Status;
import com.increff.Constants.UserType;
import com.increff.Dao.OrderDao;
import com.increff.Dao.OrderItemDao;
import com.increff.Dao.ProductDao;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.OrderAllocatedData;
import com.increff.Model.OrderChannelRequestDto;
import com.increff.Model.OrderItemCsvDto;
import com.increff.Pojo.*;
import com.increff.Service.BinService;
import com.increff.Service.ChannelService;
import com.increff.Service.OrderService;
import com.increff.Service.UserService;
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
    
    
    private UserService userService;
    private ChannelService channelService;
    private OrderDao orderDao;
    private ProductDao productDao;
    private OrderItemDao orderItemDao;
    private InventoryServiceImpl inventoryService;
    
    private BinService binService;
    
    private InvoiceServiceImpl invoiceService;
    
    @Autowired
    public OrderServiceImpl(UserService userService, ChannelService channelService, OrderDao orderDao, ProductDao productDao,
                            OrderItemDao orderItemDao, InventoryServiceImpl inventoryService,
                            BinService binService, InvoiceServiceImpl invoiceService) {
        this.userService = userService;
        this.channelService = channelService;
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.orderItemDao = orderItemDao;
        this.inventoryService = inventoryService;
        this.binService = binService;
        this.invoiceService = invoiceService;
    }
    
    @Transactional(rollbackOn = ApiGenericException.class)
    @Override
    public List<OrderItem> createOrderInternalChannel(String customerName, String clientName, String channelOrderId, List<OrderItemCsvDto> orders) {
        //checking whether customer exist or not
        List<OrderItem> result = null;
        Optional<User> customer = userService.getUserByNameAndType(customerName, UserType.CUSTOMER);
        if (!customer.isPresent() || !customer.get().getType().getValue().equals("Customer")) {
            throw new ApiGenericException("Customer not present with name " + customerName);
        }
        //checking client exist or not
        Optional<User> client = userService.getUserByNameAndType(clientName, UserType.CLIENT);
        if (!client.isPresent() || !client.get().getType().equals(UserType.CLIENT)) {
            throw new ApiGenericException("Client  not present with name" + clientName);
        }
        if (isChannelOrderDuplicate(channelOrderId)) {
            throw new ApiGenericException("channel OrderId duplicate");
        }
        Order order = orderDao.addOrder(upsertOrder(customer.get().getUserId(), client.get().getUserId(), channelOrderId, "INTERNAL", InvoiceType.SELF));
        result = upsertOrderItemDetailsInternal(client.get().getUserId(), order.getOrderId(), orders);
        return result;
    }
    
    @Override
    @Transactional
    public List<OrderItem> createOrderExternalChannel(OrderChannelRequestDto orderRequest) {
        List<OrderItem> result = null;
        //repetitive code  can be used in a method
        Optional<User> customer = Optional.ofNullable(userService.findUserById(orderRequest.getCustomerId()));
        if (!customer.isPresent()) {
            throw new ApiGenericException("Customer not present with id " + orderRequest.getCustomerId());
        }
        //checking client exist or not
        Optional<User> client = Optional.ofNullable(userService.findUserById(orderRequest.getClientId()));
        if (!client.isPresent() || !client.get().getType().equals(UserType.CLIENT)) {
            throw new ApiGenericException("Client  not present with id " + orderRequest.getClientId());
        }
        
        Channel channel = channelService.getChannelByChannelName(orderRequest.getChannelName());
        
        if (isChannelOrderDuplicate(orderRequest.getChannelOrderId())) {
            throw new ApiGenericException("channel OrderId duplicate");
        }
        Order order = orderDao.addOrder(upsertOrder(orderRequest.getCustomerId(), orderRequest.getClientId(),
                orderRequest.getChannelOrderId(), orderRequest.getChannelName(), InvoiceType.CHANNEL));
        result = upsertOrderItemDetailsExternal(orderRequest.getClientId(), order.getOrderId(),
                channel.getChannelId(), orderRequest.getOrderItems());
        
        if (CollectionUtils.isEmpty(result)) {
            throw new ApiGenericException("Order not created as All the provided SkuIds not present ");
        }
        return result;
    }
    
    
    @Override
    @Transactional(rollbackOn = ApiGenericException.class)
    public OrderAllocatedData allocateOrderPerId(Long orderId) {
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
            Inventory inventory = inventoryService.getInventoryByGlobalSkuId(orderItem.getGlobalSkuId());
            
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
            return new OrderAllocatedData(false, res);
        }
        return new OrderAllocatedData(true, res);
        
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
        List<String> skuIdsNotPresent = new ArrayList<>();
        for (OrderItemCsvDto order : orderItems) {
            Long globalSkuId = channelService.getGlobalSkuIDByClientIdAndChannelIdAndSkuId(clientId, channelId, order.getClientSkuId());
            if (globalSkuId == null) {
                skuIdsNotPresent.add(order.getClientSkuId());
            } else {
                Double price = productDao.findMrpByGlobalSkuID(globalSkuId);
                order.setSellingPricePerUnit(price);
                orderItemList.add(OrderItem.builder().orderId(orderId).globalSkuId(globalSkuId).
                        orderedQuantity(order.getOrderedQuantity()).sellingPricePerUnit(price)
                        .allocatedQuantity(0l).fulfilledQuantity(0l).build());
            }
        }
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new ApiGenericException("All the provided SkuIds not present or channel Listing is not provided");
        }
        if (skuIdsNotPresent.size() != 0) {
            logger.info("some of the Sku ids are not exist or channel listing not provided");
            throw new ApiGenericException("These Sku Ids Donot have channelListings for client", skuIdsNotPresent);
        }
        return orderItemDao.addOrderItems(orderItemList);
        
    }
    
    private List<OrderItem> upsertOrderItemDetailsInternal(Long clientId, Long orderId, List<OrderItemCsvDto> orders) {
        List<OrderItem> orderItemList = new ArrayList<>();
        //for each product
        for (OrderItemCsvDto order : orders) {
            Product savedProduct = productDao.getProductByClientIdAndClientSkuId(clientId, order.getClientSkuId());
            if (savedProduct == null) {
                throw new ApiGenericException("Product not exists for order with SKuID", order.getClientSkuId());
            }
            orderItemList.add(OrderItem.builder().orderId(orderId).globalSkuId(savedProduct.getGlobalSkuId()).
                    orderedQuantity(order.getOrderedQuantity()).sellingPricePerUnit(order.getSellingPricePerUnit())
                    .allocatedQuantity(0l).fulfilledQuantity(0l).build());
            
        }
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new ApiGenericException("All the provided SkuIds not present");
        }
        orderItemDao.addOrderItems(orderItemList);
        return orderItemList;
    }
    
    private Order upsertOrder(Long customerId, Long clientId, String channelOrderId, String channelname, InvoiceType type) {
        Channel channel = channelService.getChannelByNameAndType(channelname, type);
        
        Order order = Order.builder().customerId(customerId).clientId(clientId)
                .channelId(channel.getChannelId()).channelOrderId(channelOrderId).status(Status.CREATED).build();
        
        return order;
    }
    
    private boolean isChannelOrderDuplicate(String channelOrderId) {
        
        Long isChannelOrderIdExist = orderDao.checkChannelOrderIdExist(channelOrderId);
        
        return isChannelOrderIdExist == 1l ? true : false;
    }
    
    private void checkCustomerAndClientValidation(String customerName, String clientName) {
        Optional<User> customer = userService.getUserByNameAndType(customerName, UserType.CUSTOMER);
        if (!customer.isPresent() || !customer.get().getType().getValue().equals("Customer")) {
            throw new ApiGenericException("Customer not present with name " + customerName);
        }
        //checking client exist or not
        Optional<User> client = userService.getUserByNameAndType(clientName, UserType.CLIENT);
        if (!client.isPresent() || !client.get().getType().equals(UserType.CLIENT)) {
            throw new ApiGenericException("Client  not present with name" + clientName);
        }
    }
    
}

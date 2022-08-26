package com.increff.Service.impl;

import com.increff.Constants.InvoiceType;
import com.increff.Constants.Status;
import com.increff.Constants.UserType;
import com.increff.Dao.*;
import com.increff.Dto.OrderChannelRequestDto;
import com.increff.Dto.OrderItemCsvDto;
import com.increff.Exception.ApiGenericException;
import com.increff.Exception.CSVFileParsingException;
import com.increff.Model.*;
import com.increff.Service.BinService;
import com.increff.Service.OrderService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    
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
    
    
    @Transactional
    @Override
    public List<OrderItem> createOrderInternalChannel(Long customerId, Long clientId, String channelOrderId, MultipartFile orderItems) {
        //checking whether customer exist or not
        List<OrderItem> result = null;
        Optional<User> customer = Optional.ofNullable(userDao.findUserById(customerId));
        if (!customer.isPresent() || !customer.get().getType().getValue().equals("Customer")) {
            throw new ApiGenericException("Customer not present with id " + customerId);
        }
        //checking client exist or not
        Optional<User> client = Optional.ofNullable(userDao.findUserById(clientId));
        if (!client.isPresent() || !client.get().getType().equals(UserType.CLIENT)) {
            throw new ApiGenericException("Client  not present with id " + clientId);
        }
        if (isChannelOrderDuplicate(channelOrderId)) {
            throw new ApiGenericException("channel OrderId duplicate");
        }
        //save order details
//        Order ord=upsertOrder(customerId, clientId, channelOrderId, "INTERNAL", InvoiceType.SELF);
        Order order = orderDao.addOrder(upsertOrder(customerId, clientId, channelOrderId, "INTERNAL", InvoiceType.SELF));
        result = upsertOrderItemDetailsInternal(clientId, order.getOrderId(), orderItems);
        return result;
    }
    
    @Override
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
        return result;
        
    }
    
    private boolean isChannelOrderDuplicate(String channelOrderId) {
        
        Long isChannelOrderIdExist = orderDao.checkChannelOrderIdExist(channelOrderId);
        return isChannelOrderIdExist == 0l ? false : true;
    }
    
    private List<OrderItem> upsertOrderItemDetailsInternal(Long clientId, Long orderId, MultipartFile orderItems) {
        List<OrderItemCsvDto> orders = null;
        List<OrderItem> orderItemList = new ArrayList<>();
        try {
            orders = new CsvToBeanBuilder(new InputStreamReader(new ByteArrayInputStream(orderItems.getBytes())))
                    .withType(OrderItemCsvDto.class).withSkipLines(1).build().parse();
        } catch (Exception e) {
            throw new CSVFileParsingException(e.getCause() + e.getMessage());
        }
        Set<String> skuIds = orders.stream().map(ord -> ord.getClientSkuId()).collect(Collectors.toSet());
        if (Integer.compare(orders.size(), skuIds.size()) != 0) {
            throw new ApiGenericException("Duplicate Sku present in CSV file");
        }
        
        //for each product
        for (OrderItemCsvDto order : orders) {
            Product savedProduct = productDao.getProductForProductByClientIdAndClientSkuId(clientId, order.getClientSkuId());
            if (savedProduct != null) {
                orderItemList.add(OrderItem.builder().orderId(orderId).globalSkuId(savedProduct.getGlobalSkuId()).
                        orderedQuantity(order.getOrderedQuantity()).sellingPricePerUnit(order.getSellingPricePerUnit())
                        .allocatedQuantity(0l).fulfilledQuantity(0l).build());
            }
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
    @Transactional
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
        
        List<OrderItem> orderItems = orderItemDao.fetchOrderItemByOrderId(orderId);
        
        boolean isOrderAllocated = true;
        for (OrderItem orderItem : orderItems) {
            Inventory inventory = inventoryDao.getInvetoryBySkuId(orderItem.getGlobalSkuId());
            Long allocatedItem = Math.min((orderItem.getOrderedQuantity() - orderItem.getAllocatedQuantity())
                    , inventory.getAvailableQuantity());
            
            //orderItem.setOrderedQuantity(orderItem.getOrderedQuantity() - allocatedItem);
            orderItem.setAllocatedQuantity(orderItem.getAllocatedQuantity() + allocatedItem);
            //if ordered quantity is not same as allocated means inventory has less item
            if (Long.compare(orderItem.getOrderedQuantity(), orderItem.getAllocatedQuantity()) != 0) {
                isOrderAllocated = false;
            }
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - allocatedItem);
            inventory.setAllocatedQuantity(inventory.getAllocatedQuantity() + allocatedItem);
            orderItemDao.addSingleOrderItem(orderItem);
            inventoryDao.addInventoryEntity(inventory);
            binService.removeProductsFromBinAfterAllocation(orderItem.getGlobalSkuId(), allocatedItem);
            res.add(orderItem);
        }
        if (isOrderAllocated == true) {
            order.setStatus(Status.ALLOCATED);
            orderDao.addOrder(order);
        }
        
        return res;
    }
    
    @Override
    @Transactional
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
        
    }
    
    private List<OrderItem> upsertOrderItemDetailsExternal(Long clientId, Long orderId, Long channelId, List<OrderItemCsvDto> orderItems) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (OrderItemCsvDto order : orderItems) {
            Long globalSkuId = channelDao.getGlobalSkuIDByChannelIdAndSkuId(clientId, channelId, order.getClientSkuId());
            Double price = productDao.findMrpByGlobalSkuID(globalSkuId);
            if (globalSkuId != null) {
                order.setSellingPricePerUnit(price);
                orderItemList.add(OrderItem.builder().orderId(orderId).globalSkuId(globalSkuId).
                        orderedQuantity(order.getOrderedQuantity()).sellingPricePerUnit(order.getSellingPricePerUnit()).build());
            }
        }
        return orderItemDao.addOrderItems(orderItemList);
        
    }
    
}

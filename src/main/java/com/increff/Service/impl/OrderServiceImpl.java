package com.increff.Service.impl;

import com.increff.Constants.InvoiceType;
import com.increff.Constants.Status;
import com.increff.Constants.UserType;
import com.increff.Dao.*;
import com.increff.Dto.OrderChannelRequestDto;
import com.increff.Dto.OrderItemCsvDto;
import com.increff.Exception.UserException;
import com.increff.Model.*;
import com.increff.Service.BinService;
import com.increff.Service.OrderService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private UserDao userDao;
    private ChannelDao channelDao;


    private OrderDao orderDao;

    private ProductDao productDao;

    private OrderItemDao orderItemDao;

    private InventoryDao inventoryDao;

    private BinDao binDao;
    @Autowired
    private BinService binService;

    @Autowired
    public OrderServiceImpl(UserDao userDao, ChannelDao channelDao,
                            OrderDao orderDao, ProductDao productDao, OrderItemDao orderItemDao,
                            InventoryDao inventoryDao, BinDao binDao) {
        this.userDao = userDao;
        this.channelDao = channelDao;
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.orderItemDao = orderItemDao;
        this.inventoryDao = inventoryDao;
        this.binDao = binDao;
    }

    @Transactional
    @Override
    public List<OrderItem> createOrderInternalChannel(Long customerId, Long clientId, String channelOrderId, MultipartFile orderItems) {
        //checking whether customer exist or not
        List<OrderItem> result = null;
        Optional<User> customer = Optional.ofNullable(userDao.findUserById(customerId));
        if (!customer.isPresent() || !customer.get().getType().getValue().equals("Customer")) {
            throw new UserException("Customer not present with id " + customerId);
        }
        //checking client exist or not
        Optional<User> client = Optional.ofNullable(userDao.findUserById(clientId));
        if (!client.isPresent() || !client.get().getType().equals(UserType.CLIENT)) {
            throw new UserException("Client  not present with id " + clientId);
        }
        //save order details
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
            throw new UserException("Customer not present with id " + orderRequest.getCustomerId());
        }
        //checking client exist or not
        Optional<User> client = Optional.ofNullable(userDao.findUserById(orderRequest.getClientId()));
        if (!client.isPresent() || !client.get().getType().equals(UserType.CLIENT)) {
            throw new UserException("Client  not present with id " + orderRequest.getClientId());
        }

        Optional<Channel> channel = channelDao.checkChannelExistOrNot(orderRequest.getChannelName());
        if (!channel.isPresent()) {
            throw new UserException("channel nahi hai");

        }
        Order order = orderDao.addOrder(upsertOrder(orderRequest.getCustomerId(), orderRequest.getClientId(),
                orderRequest.getChannelOrderId(), orderRequest.getChannelName(), InvoiceType.CHANNEL));
        result = upsertOrderItemDetailsExternal(orderRequest.getClientId(), order.getOrderId(),
                channel.get().getChannelId(), orderRequest.getOrderItems());
        return result;

    }


    private List<OrderItem> upsertOrderItemDetailsInternal(Long clientId, Long orderId, MultipartFile orderItems) {
        List<OrderItemCsvDto> orders = null;
        List<OrderItem> orderItemList = new ArrayList<>();
        try {
            orders = new CsvToBeanBuilder(new InputStreamReader(new ByteArrayInputStream(orderItems.getBytes())))
                    .withType(OrderItemCsvDto.class).withSkipLines(1).build().parse();
        } catch (IOException e) {
            e.getCause();
        }

        //for each product
        for (OrderItemCsvDto order : orders) {
            Long globalSkuId = productDao.getGlobalIdForProductByClientIdAndClientSkuId(clientId, order.getClientSkuId());
            if (globalSkuId != null) {
                orderItemList.add(OrderItem.builder().orderItemId(orderId).globalSkuId(globalSkuId).
                        orderedQuantity(order.getOrderedQuantity()).sellingPricePerUnit(order.getSellingPricePerUnit()).build());
            }
        }
        orderItemDao.addOrderItems(orderItemList);
        return orderItemList;
    }

    private List<OrderItem> upsertOrderItemDetailsExternal(Long clientId, Long orderId, Long channelId, List<OrderItemCsvDto> orderItems) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (OrderItemCsvDto order : orderItems) {
            Long globalSkuId = channelDao.getGlobalSkuIDByChannelIdAndSkuId(clientId, channelId, order.getClientSkuId());
            Double price = productDao.findMrpByGlobalSkuID(globalSkuId);
            if (globalSkuId != null) {
                order.setSellingPricePerUnit(price);
                orderItemList.add(OrderItem.builder().orderItemId(orderId).globalSkuId(globalSkuId).
                        orderedQuantity(order.getOrderedQuantity()).sellingPricePerUnit(order.getSellingPricePerUnit()).build());
            }
        }
        orderItemDao.addOrderItems(orderItemList);
        return orderItemList;
    }


    private Order upsertOrder(Long customerId, Long clientId, String channelOrderId, String channelname, InvoiceType type) {
        Long channelId = channelDao.getChannelIdByNameAndType(channelname, type);
        if (channelId == null) {
            throw new UserException("channel Not exist");
        }
        Order order = Order.builder().customerId(customerId).clientId(clientId)
                .channelId(channelId).channelOrderId(channelOrderId).status(Status.CREATED).build();

//                orderConverter.convertOrderDtoToOrderEntity(new OrderDto(customerId, clientId,
//                channelId, channelOrderId, Status.CREATED));
        return order;
    }

    @Override
    @Transactional
    public List<OrderItem> allocateOrderPerId(Long orderId) {
        Order order = orderDao.findOrderByOrderId(orderId);
        List<OrderItem> res = new ArrayList<>();
        if (order == null) {
            throw new UserException("Order not exist");
        }
        if (order.getStatus().equals(Status.ALLOCATED)) {
            //order already allocated
            throw new UserException("Order already Allocated");
        }

        List<OrderItem> orderItems = orderItemDao.fetchOrderItemByOrderId(orderId);

        boolean isOrderAllocated = true;
        for (OrderItem orderItem : orderItems) {
            Inventory inventory = inventoryDao.getInvetoryIdBySkuId(orderItem.getGlobalSkuId());
            Long allocatedItem = Math.min((orderItem.getOrderedQuantity() - orderItem.getAllocatedQuantity())
                    , inventory.getAvailableQuantity());

            //orderItem.setOrderedQuantity(orderItem.getOrderedQuantity() - allocatedItem);
            orderItem.setAllocatedQuantity(orderItem.getAllocatedQuantity() + allocatedItem);
            //if ordered quantity is not 0 means inventory has less item
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
}

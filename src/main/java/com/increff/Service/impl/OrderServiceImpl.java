package com.increff.Service.impl;

import com.increff.Constants.InvoiceType;
import com.increff.Constants.Status;
import com.increff.Constants.UserType;
import com.increff.Dao.*;
import com.increff.Dto.Converter.OrderConverter;
import com.increff.Dto.OrderDto;
import com.increff.Dto.OrderItemCsvDto;
import com.increff.Exception.UserException;
import com.increff.Model.Order;
import com.increff.Model.OrderItem;
import com.increff.Model.User;
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

    private OrderConverter orderConverter;

    private OrderDao orderDao;

    private ProductDao productDao;

    private OrderItemDao orderItemDao;

    @Autowired
    public OrderServiceImpl(UserDao userDao, ChannelDao channelDao, OrderConverter orderConverter, OrderDao orderDao, ProductDao productDao, OrderItemDao orderItemDao) {
        this.userDao = userDao;
        this.channelDao = channelDao;
        this.orderConverter = orderConverter;
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.orderItemDao = orderItemDao;
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
        result = upsertOrderItemDetails(clientId, order.getOrderId(), orderItems);
        return result;
    }

    private List<OrderItem> upsertOrderItemDetails(Long clientId, Long orderId, MultipartFile orderItems) {
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
            Long globalSkuId = productDao.getGlobalIdForProductByClientSkuIdAndClientId(clientId, order.getClientSkuId());
            if (globalSkuId != null) {
                orderItemList.add(orderConverter.convertOrderItemFromOrdenrItemCsvDto(globalSkuId, orderId, order));
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
        Order order = orderConverter.convertOrderDtoToOrderEntity(new OrderDto(customerId, clientId,
                channelId, channelOrderId, Status.CREATED));
        return order;
    }
}

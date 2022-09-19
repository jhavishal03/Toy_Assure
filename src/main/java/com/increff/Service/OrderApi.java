package com.increff.Service;

import com.increff.Dao.OrderDao;
import com.increff.Dao.OrderItemDao;
import com.increff.Exception.ApiGenericException;
import com.increff.Pojo.OrderItemPojo;
import com.increff.Pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Service
public class OrderApi {
    
    private static Logger logger = Logger.getLogger(OrderApi.class.getName());
    
    
    private OrderDao orderDao;
    private OrderItemDao orderItemDao;
    
    @Autowired
    public OrderApi(OrderDao orderDao, OrderItemDao orderItemDao) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
    }
    
    public OrderPojo addOrderPojo(OrderPojo orderPojo) {
        return orderDao.addOrder(orderPojo);
    }
    
    @Transactional
    public OrderPojo updateOrderPojo(OrderPojo orderPojo) {
        return orderDao.updateOrder(orderPojo);
    }
    
    
    public OrderPojo getOrderPojoById(Long orderId) {
        OrderPojo orderPojo = orderDao.findOrderByOrderId(orderId);
        if (orderPojo == null) {
            throw new ApiGenericException("Order Entity not exist");
        }
        return orderPojo;
    }
    
    
    public List<OrderItemPojo> getOrderDetailsByOrderId(Long orderId) {
        logger.info("getOrderDetailsBy Id started");
        OrderPojo orderPojo = this.getOrderPojoById(orderId);
        
        List<OrderItemPojo> orderItemPojoList = orderItemDao.fetchOrderItemByOrderId(orderId);
        logger.info("OrderPojo details are" + orderItemPojoList);
        logger.info("getOrderDetailsBy Id End");
        return orderItemPojoList;
    }
    
    
    public boolean isChannelOrderDuplicate(String channelOrderId) {
        
        Long isChannelOrderIdExist = orderDao.checkChannelOrderIdExist(channelOrderId);
        
        return isChannelOrderIdExist == 1l ? true : false;
    }
    
    public List<OrderItemPojo> addOrderItemList(List<OrderItemPojo> orderItems) {
        return orderItemDao.addOrderItems(orderItems);
    }
    
}

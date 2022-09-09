package com.increff.Dao;

import com.increff.Pojo.Order;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

@Repository
public class OrderDao extends AbstractDao {
    
    
    private static String findOrderByOrderId = "select o from Order o where orderId=:orderId";
    
    private static String checkChannelOrderIdExist = "select count(o) from Order o where channelOrderId=:chnOId  ";
    
    
    public Order addOrder(Order order) {
        em.persist(order);
        return order;
    }
    
    public Long checkChannelOrderIdExist(String channelOrderId) {
        TypedQuery<Long> query = getQuery(checkChannelOrderIdExist, Long.class);
        query.setParameter("chnOId", channelOrderId);
        return query.getSingleResult();
    }
    
    public Order findOrderByOrderId(Long orderId) {
        TypedQuery<Order> query = getQuery(findOrderByOrderId, Order.class);
        query.setParameter("orderId", orderId);
        return getSingle(query);
    }
    
    
}

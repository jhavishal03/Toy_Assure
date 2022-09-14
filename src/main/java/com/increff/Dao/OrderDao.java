package com.increff.Dao;

import com.increff.Pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

@Repository
public class OrderDao extends AbstractDao {
    
    
    private static String findOrderByOrderId = "select o from OrderPojo o where orderId=:orderId";
    
    private static String checkChannelOrderIdExist = "select count(o) from OrderPojo o where channelOrderId=:chnOId  ";
    
    public OrderPojo addOrder(OrderPojo orderPojo) {
        em.persist(orderPojo);
        return orderPojo;
    }
    
    public OrderPojo updateOrder(OrderPojo orderPojo) {
        return em.merge(orderPojo);
        
    }
    
    public Long checkChannelOrderIdExist(String channelOrderId) {
        TypedQuery<Long> query = getQuery(checkChannelOrderIdExist, Long.class);
        query.setParameter("chnOId", channelOrderId);
        return query.getSingleResult();
    }
    
    public OrderPojo findOrderByOrderId(Long orderId) {
        TypedQuery<OrderPojo> query = getQuery(findOrderByOrderId, OrderPojo.class);
        query.setParameter("orderId", orderId);
        return getSingle(query);
    }
    
    
}

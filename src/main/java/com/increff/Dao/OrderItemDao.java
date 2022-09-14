package com.increff.Dao;

import com.increff.Pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao {
    
    
    private static String findOrderItemsByOrderId = "select o from OrderItemPojo o where orderId=:orderId";
    
    
    public List<OrderItemPojo> addOrderItems(List<OrderItemPojo> orderItemPojoList) {
        List<OrderItemPojo> result = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            em.persist(orderItemPojo);
            result.add(orderItemPojo);
        }
        return result;
    }
    
    @Transactional
    public OrderItemPojo addSingleOrderItem(OrderItemPojo order) {
        em.persist(order);
        return order;
    }
    
    @Transactional
    public OrderItemPojo updateSingleOrderItem(OrderItemPojo order) {
        return em.merge(order);
        
    }
    
    public List<OrderItemPojo> fetchOrderItemByOrderId(Long orderId) {
        TypedQuery<OrderItemPojo> query = getQuery(findOrderItemsByOrderId, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }
    
}

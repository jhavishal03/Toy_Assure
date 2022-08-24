package com.increff.Dao;

import com.increff.Model.OrderItem;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao {


    private static String findOrderItemsByOrderId = "select o from OrderItem o where orderId=:orderId";

    @Transactional
    public List<OrderItem> addOrderItems(List<OrderItem> orderItemList) {
        List<OrderItem> result = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            em.persist(orderItem);
            result.add(orderItem);
        }
        return result;
    }

    @Transactional
    public OrderItem addSingleOrderItem(OrderItem order) {
        em.persist(order);
        return order;

    }

    public List<OrderItem> fetchOrderItemByOrderId(Long orderId) {
        TypedQuery<OrderItem> query = getQuery(findOrderItemsByOrderId, OrderItem.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }

}

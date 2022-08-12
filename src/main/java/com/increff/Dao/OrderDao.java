package com.increff.Dao;

import com.increff.Model.Order;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Repository
public class OrderDao extends AbstractDao {


    private static String findOrderByOrderId = "select o from Order o where orderId=:orderId";

    @Transactional
    public Order addOrder(Order order) {
        em.persist(order);
        return order;
    }

    public Order findOrderByOrderId(Long orderId) {
        TypedQuery<Order> query = getQuery(findOrderByOrderId, Order.class);
        query.setParameter("orderId", orderId);
        return getSingle(query);
    }


}

package com.increff.Dao;

import com.increff.Model.Order;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public class OrderDao extends AbstractDao {


    @Transactional
    public Order addOrder(Order order) {
        em.persist(order);
        return order;
    }


}

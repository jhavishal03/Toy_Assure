package com.increff.Dao;

import com.increff.Model.OrderItem;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao {

    @Transactional
    public List<OrderItem> addOrderItems(List<OrderItem> orderItemList) {
        List<OrderItem> result = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            em.persist(orderItem);
            result.add(orderItem);
        }
        return result;
    }
}

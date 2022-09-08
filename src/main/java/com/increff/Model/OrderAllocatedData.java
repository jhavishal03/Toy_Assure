package com.increff.Model;

import com.increff.Pojo.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class OrderAllocatedData {
    boolean isAllocated;
    List<OrderItem> orderItems;
}

package com.increff.Model;

import com.increff.Pojo.OrderItemPojo;
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
    List<OrderItemPojo> orderItemPojos;
}

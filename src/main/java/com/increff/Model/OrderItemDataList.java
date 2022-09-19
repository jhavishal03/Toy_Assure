package com.increff.Model;


import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "OrderItemDataList")
public class OrderItemDataList {
    private LocalDate time;
    private Long orderId;
    private String clientName;
    private Long totalQuantity;
    private Long totalAmount;
    private List<OrderItemsXmlForm> orderItems;
    
    public OrderItemDataList() {
    }
}

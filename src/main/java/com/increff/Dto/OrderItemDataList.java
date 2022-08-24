package com.increff.Dto;


import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "OrderItemDataLisr")
public class OrderItemDataList {
    private LocalDate time;
    private Long orderId;
    private String clientName;
    private Long totalQuantity;
    private Long totalAmount;
    private List<OrderItemsXmlDto> orderItems;
    
    public OrderItemDataList() {
    }
}

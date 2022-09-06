package com.increff.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemsXmlDto {
    private String skuId;
    private String productName;
    private Long quantity;
    private Double sellingPrice;
    private Long amount;
}

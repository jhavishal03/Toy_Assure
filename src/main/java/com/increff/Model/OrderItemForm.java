package com.increff.Model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemForm {
    private String channelSkuId;
    private Long orderedQuantity;
}

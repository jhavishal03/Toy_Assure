package com.increff.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "assure_OrderItem")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderItem extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    @Min(0)
    private Long orderId;
    @Min(0)
    private Long globalSkuId;
    private Long orderedQuantity = 0L;

    private Long allocatedQuantity = 0L;
    private Long fulfilledQuantity = 0L;
    @Min(0)
    private Double sellingPricePerUnit;

}

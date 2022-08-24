package com.increff.Model;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "assure_OrderItem")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItem extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    @Min(0)
    private Long orderId;
    @Min(0)
    private Long globalSkuId;
    private Long orderedQuantity = 0L;
    
    @ColumnDefault("0")
    private Long allocatedQuantity = 0L;
    @ColumnDefault("0")
    private Long fulfilledQuantity = 0L;
    @Min(0)
    private Double sellingPricePerUnit;
    
}

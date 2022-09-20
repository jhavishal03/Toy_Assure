package com.increff.Pojo;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "assure_OrderItem")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderItemPojo extends BaseModel {
    //todo orderquantity nullable false
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    @Column(nullable = false)
    private Long orderId;
    @Column(nullable = false)
    private Long globalSkuId;
    private Long orderedQuantity;
    
    @ColumnDefault("0")
    private Long allocatedQuantity = 0L;
    @ColumnDefault("0")
    private Long fulfilledQuantity = 0L;
    @ColumnDefault("0")
    private Double sellingPricePerUnit;
    
}

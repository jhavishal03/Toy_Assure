package com.increff.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "assure_Inventory")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryPojo extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;
    @Column(nullable = false)
    private Long globalSkuId;
    @Column(nullable = false)
    private Long availableQuantity;
    private Long allocatedQuantity = 0L;
    private Long fulfilledQuantity = 0L;
}

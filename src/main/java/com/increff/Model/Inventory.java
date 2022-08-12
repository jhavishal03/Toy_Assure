package com.increff.Model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "assure_Inventory")
@Builder
@RequiredArgsConstructor
@Data
public class Inventory extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;
    @Min(0)
    private Long globalSkuId;
    @NotNull
    private Long availableQuantity;
    private Long allocatedQuantity = 0L;
    private Long fulfilledQuantity = 0L;
}

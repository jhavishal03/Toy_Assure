package com.increff.Model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "assure_Product")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Product extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long globalSkuId;
    private String clientSkuId;
    private Long clientId;
    @NotEmpty
    private String name;
    @NotEmpty
    private String brandId;
    @Min(0)
    private double mrp;
    @NotEmpty
    private String description;
}

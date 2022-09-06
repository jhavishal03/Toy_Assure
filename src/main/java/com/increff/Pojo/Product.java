package com.increff.Pojo;

import lombok.*;

import javax.persistence.*;

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
    @Column(nullable = false)
    private String clientSkuId;
    @Column(nullable = false)
    private Long clientId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String brandId;
    @Column(nullable = false)
    private double mrp;
    private String description;
}

package com.increff.Pojo;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "assure_BinSku")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BinSku extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long binSkuId;
    
    @Column(nullable = false)
    private Long binId;
    @Column(nullable = false)
    private Long globalSkuId;
    @Column(nullable = false)
    private Long quantity;
}

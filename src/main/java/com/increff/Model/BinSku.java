package com.increff.Model;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    @NotNull
    private Long binId;
    @NotNull
    private Long globalSkuId;
    @NotNull
    private Long quantity;
}

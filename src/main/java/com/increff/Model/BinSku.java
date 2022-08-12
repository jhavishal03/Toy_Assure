package com.increff.Model;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "assure_BinSku")
@Builder
@RequiredArgsConstructor
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

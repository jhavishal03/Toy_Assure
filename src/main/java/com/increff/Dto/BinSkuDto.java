package com.increff.Dto;


import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BinSkuDto {
    @CsvBindByPosition(position = 0)
    private Long binId;
    @CsvBindByPosition(position = 1)
    private Long globalSkuId;
    @CsvBindByPosition(position = 2)
    private Long quantity;

}

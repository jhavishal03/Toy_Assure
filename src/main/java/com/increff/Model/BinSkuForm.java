package com.increff.Model;


import com.increff.Util.MustAValidNumber;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.validators.PreAssignmentValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BinSkuForm {
    @CsvBindByPosition(position = 0, required = true)
    @PreAssignmentValidator(validator = MustAValidNumber.class)
    private Long binId;
    @CsvBindByPosition(position = 1, required = true)
    private String clientSkuId;
    @CsvBindByPosition(position = 2, required = true)
    @PreAssignmentValidator(validator = MustAValidNumber.class)
    private Long quantity;
    
}

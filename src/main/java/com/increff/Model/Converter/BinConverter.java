package com.increff.Model.Converter;

import com.increff.Model.BinSkuDto;
import com.increff.Pojo.BinSku;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BinConverter {
    
    public List<BinSku> convertBinSkuDtoToBin(List<BinSkuDto> binSkuDtos) {
        List<BinSku> result = new ArrayList<>();
        for (BinSkuDto bin : binSkuDtos) {
            result.add(
                    BinSku.builder().binId(bin.getBinId()).
                            quantity(bin.getQuantity()).build());
        }
        return result;
    }
}

package com.increff.Dto.Converter;

import com.increff.Dto.BinSkuDto;
import com.increff.Model.BinSku;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BinConverter {

    public List<BinSku> convertBinSkuDtoToBin(List<BinSkuDto> binSkuDtos) {
        List<BinSku> result = new ArrayList<>();
        for (BinSkuDto bin : binSkuDtos) {
            result.add(new BinSku(bin.getBinId(), bin.getGlobalSkuId(), bin.getQuantity()));
        }
        return result;
    }
}

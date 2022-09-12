package com.increff.Model.Converter;

import com.increff.Model.BinSkuForm;
import com.increff.Pojo.BinSku;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BinConverter {
    
    public List<BinSku> convertBinSkuDtoToBin(List<BinSkuForm> binSkuForms) {
        List<BinSku> result = new ArrayList<>();
        for (BinSkuForm bin : binSkuForms) {
            result.add(
                    BinSku.builder().binId(bin.getBinId()).
                            quantity(bin.getQuantity()).build());
        }
        return result;
    }
}

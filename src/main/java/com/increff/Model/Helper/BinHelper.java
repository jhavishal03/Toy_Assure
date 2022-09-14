package com.increff.Model.Helper;

import com.increff.Model.BinSkuForm;
import com.increff.Pojo.BinSkuPojo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BinHelper {
    
    public List<BinSkuPojo> convertBinSkuDtoToBin(List<BinSkuForm> binSkuForms) {
        List<BinSkuPojo> result = new ArrayList<>();
        for (BinSkuForm bin : binSkuForms) {
            result.add(
                    BinSkuPojo.builder().binId(bin.getBinId()).
                            quantity(bin.getQuantity()).build());
        }
        return result;
    }
}

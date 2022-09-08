package com.increff.Util;

import com.increff.Exception.ApiGenericException;
import com.increff.Model.BinSkuDto;

import java.util.List;

public class PropertyValidator {
    
    public static void validateBinSkuList(List<BinSkuDto> binData) {
        for (BinSkuDto bin : binData) {
            if (bin.getBinId() < 0) {
                throw new ApiGenericException("Negative Bin Id exist please update CSV");
            }
            if (bin.getQuantity() < 0) {
                throw new ApiGenericException("Negative Quantity exist of bin " + bin.getBinId());
            }
        }
    }
}
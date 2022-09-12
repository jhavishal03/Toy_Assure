package com.increff.Service;

import com.increff.Model.BinSkuForm;
import com.increff.Pojo.BinSku;

import java.util.List;

public interface BinService {
    
    public List<Long> addBinToSystem(int num);
    
    public List<BinSku> uploadBinData(Long clientId, List<BinSkuForm> binData);
    
    public void removeProductsFromBinAfterAllocation(Long globalSkuId, Long quantity);
    
}

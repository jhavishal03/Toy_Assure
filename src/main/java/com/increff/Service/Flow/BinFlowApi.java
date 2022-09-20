package com.increff.Service.Flow;

import com.increff.Model.BinSkuForm;
import com.increff.Pojo.BinSkuPojo;
import com.increff.Pojo.ProductPojo;
import com.increff.Service.BinApi;
import com.increff.Service.InventoryApi;
import com.increff.Service.ProductApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BinFlowApi {
    private ProductApi productApi;
    private BinApi binApi;
    private InventoryApi inventoryApi;
    
    @Autowired
    public BinFlowApi(ProductApi productApi, BinApi binApi, InventoryApi inventoryApi) {
        this.productApi = productApi;
        this.binApi = binApi;
        this.inventoryApi = inventoryApi;
    }
    
    @Transactional
    public List<BinSkuPojo> updateBinAndInventoryForBinUpload(Long clientId, List<BinSkuForm> binSkus) {
        Set<BinSkuPojo> entitiesToBeNewlyAdded = new HashSet<>();
        List<BinSkuPojo> result = new ArrayList<>();
        
        for (BinSkuForm binDto : binSkus) {
            Long changeInProductQuantity = 0L;
            ProductPojo productPojo = productApi.findProductByClientIdAndSkuId(clientId, binDto.getClientSkuId());
            Long globalSku = productPojo.getGlobalSkuId();
            
            Optional<BinSkuPojo> getSavedBin = Optional.ofNullable(binApi.getBinSkuByBinIdandSkuId(globalSku,
                    binDto.getBinId()));
            //productPojo is added first time in bin
            if (!getSavedBin.isPresent()) {
                BinSkuPojo bin = BinSkuPojo.builder().binId(binDto.getBinId())
                        .quantity(binDto.getQuantity())
                        .globalSkuId(globalSku).build();
                entitiesToBeNewlyAdded.add(bin);
                changeInProductQuantity = binDto.getQuantity();
            } else {
                BinSkuPojo savedBin = getSavedBin.get();
                changeInProductQuantity = binDto.getQuantity() - savedBin.getQuantity();
                savedBin.setQuantity(binDto.getQuantity());
                result.add(savedBin);
            }
            //inventoryPojo update call to be made
            inventoryApi.updateInventoryAfterBinDataUpload(globalSku, changeInProductQuantity);
        }
        return binApi.addBinSkuDataList(entitiesToBeNewlyAdded);
    }
}

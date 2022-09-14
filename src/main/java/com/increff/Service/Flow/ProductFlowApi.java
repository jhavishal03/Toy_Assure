package com.increff.Service.Flow;

import com.increff.Pojo.InventoryPojo;
import com.increff.Pojo.ProductPojo;
import com.increff.Service.InventoryApi;
import com.increff.Service.ProductApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.increff.Model.Helper.DtoHelper.convertInventoryFromProductList;

@Service
public class ProductFlowApi {
    @Autowired
    private ProductApi productApi;
    @Autowired
    private InventoryApi inventoryApi;
    
    @Transactional
    public List<ProductPojo> updateProductsAndInventory(Long clientId, List<ProductPojo> productPojos,
                                                        List<String> savedClientSkuIds) {
        List<ProductPojo> res = productApi.uploadProductDetailsForClient(clientId, productPojos, savedClientSkuIds);
        List<String> productsNotAdded = productPojos.stream().filter(prd -> !savedClientSkuIds.contains(prd.getClientSkuId()))
                .map(entity -> entity.getClientSkuId()).collect(Collectors.toList());
        List<Long> productToBeAddedInInventory = res.stream().filter(prd -> productsNotAdded.contains(prd.getClientSkuId()))
                .map(prd -> prd.getGlobalSkuId()).collect(Collectors.toList());
        List<InventoryPojo> inventoryDataToBeAdded = convertInventoryFromProductList(productToBeAddedInInventory);
        return res;
    }
}

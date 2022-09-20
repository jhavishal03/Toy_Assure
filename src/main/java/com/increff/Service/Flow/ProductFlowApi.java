package com.increff.Service.Flow;

import com.increff.Pojo.ProductPojo;
import com.increff.Service.InventoryApi;
import com.increff.Service.ProductApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProductFlowApi {
    @Autowired
    private ProductApi productApi;
    @Autowired
    private InventoryApi inventoryApi;
    
    @Transactional
    public List<ProductPojo> updateProductsAndInventory(Long clientId, List<ProductPojo> productPojos) {
        
        List<ProductPojo> result = new ArrayList<>();
        List<ProductPojo> productsToBeAdded = new ArrayList<>();
        List<ProductPojo> productsToBeUpdated = new ArrayList<>();
        for (ProductPojo product : productPojos) {
            ProductPojo productPojo = productApi.getProductByClientIdAndClientSku(clientId, product.getClientSkuId());
            if (Objects.isNull(productPojo)) {
                productsToBeAdded.add(product);
            } else {
                productsToBeUpdated.add(productPojo);
            }
        }
        result.addAll(productApi.addProductsData(productsToBeAdded));
        result.addAll(productApi.updateDetailsForExistingProduct(clientId, productsToBeUpdated));
        return result;
    }
}

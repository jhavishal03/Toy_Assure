package com.increff.Model.Helper;

import com.increff.Model.ProductForm;
import com.increff.Pojo.ProductPojo;

import java.util.ArrayList;
import java.util.List;


public class ProductHelper {
    
    public static List<ProductPojo> productFormToProductPojo(Long clientId, List<ProductForm> productDto) {
        List<ProductPojo> productPojos = new ArrayList<>();
        for (ProductForm prd : productDto) {
            productPojos.add(prodctToProductPojo(clientId, prd));
        }
        return productPojos;
    }
    
    public static ProductPojo prodctToProductPojo(Long clientId, ProductForm productDto) {
        
        return ProductPojo.builder().clientId(clientId).brandId(productDto.getBrandId()).
                clientSkuId(productDto.getClientSkuId()).name(productDto.getName().trim().toLowerCase())
                .mrp(productDto.getMrp())
                .description(productDto.getDescription()).build();
    }
    
    
}

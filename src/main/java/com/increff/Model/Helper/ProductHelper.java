package com.increff.Model.Helper;

import com.increff.Model.ProductForm;
import com.increff.Pojo.ProductPojo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductHelper {
    public List<ProductPojo> productDtoToProductBulk(Long clientId, List<ProductForm> productDto) {
        List<ProductPojo> productPojos = new ArrayList<>();
        for (ProductForm prd : productDto) {
            productPojos.add(this.prodctDtoToProduct(clientId, prd));
        }
        return productPojos;
    }
    
    public ProductPojo prodctDtoToProduct(Long clientId, ProductForm productDto) {
        
        return ProductPojo.builder().clientId(clientId).brandId(productDto.getBrandId()).
                clientSkuId(productDto.getClientSkuId()).name(productDto.getName().trim().toLowerCase())
                .mrp(productDto.getMrp())
                .description(productDto.getDescription()).build();
    }
}

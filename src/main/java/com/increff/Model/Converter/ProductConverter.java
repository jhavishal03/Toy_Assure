package com.increff.Model.Converter;

import com.increff.Model.ProductForm;
import com.increff.Pojo.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductConverter {
    public List<Product> productDtoToProductBulk(Long clientId, List<ProductForm> productDto) {
        List<Product> products = new ArrayList<>();
        for (ProductForm prd : productDto) {
            products.add(this.prodctDtoToProduct(clientId, prd));
        }
        return products;
    }
    
    public Product prodctDtoToProduct(Long clientId, ProductForm productDto) {
        
        return Product.builder().clientId(clientId).brandId(productDto.getBrandId()).
                clientSkuId(productDto.getClientSkuId()).name(productDto.getName()).mrp(productDto.getMrp())
                .description(productDto.getDescription()).build();
    }
}

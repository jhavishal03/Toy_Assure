package com.increff.Dto.Converter;

import com.increff.Dto.ProductDto;
import com.increff.Model.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductConverter {
    public List<Product> productDtoToProductBulk(Long clientId, List<ProductDto> productDto) {
        List<Product> products = new ArrayList<>();
        for (ProductDto prd : productDto) {
            products.add(this.prodctDtoToProduct(clientId, prd));
        }
        return products;
    }

    public Product prodctDtoToProduct(Long clientId, ProductDto productDto) {
        
        return Product.builder().clientId(clientId).brandId(productDto.getBrandId()).
                clientSkuId(productDto.getClientSkuId()).name(productDto.getName()).mrp(productDto.getMrp())
                .description(productDto.getDescription()).build();
    }
}

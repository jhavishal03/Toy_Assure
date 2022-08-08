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
        Product product = new Product();
        product.setClientId(clientId);
        product.setBrandId(productDto.getBrandId());
        product.setClientSkuId(productDto.getClientSkuId());
        product.setName(productDto.getName());
        product.setMrp(productDto.getMrp());
        product.setDescription(productDto.getDescription());
        return product;
    }
}

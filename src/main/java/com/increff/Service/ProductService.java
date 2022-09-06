package com.increff.Service;

import com.increff.Model.ProductForm;
import com.increff.Pojo.Product;

import java.util.List;

public interface ProductService {
    public List<Product> uploadProductDetailsForClient(Long clientId, List<Product> products);
    
    public List<Product> uploadProductDetailsForClientList(Long clientId, List<ProductForm> productDtoList);
}

package com.increff.Service;

import com.increff.Dto.ProductDto;
import com.increff.Model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    public List<Product> uploadProductDetailsForClient(Long clientId, MultipartFile file) throws IOException;

    public List<Product> uploadProductDetailsForClientList(Long clientId, List<ProductDto> productDtoList);
}

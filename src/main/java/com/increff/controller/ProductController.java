package com.increff.controller;

import com.increff.Dto.ProductDto;
import com.increff.Model.Product;
import com.increff.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products/{clientId}/upload")
    public void uploadProductDetails(@PathVariable("clientId") Long clientId,
                                     @RequestBody MultipartFile file) throws Exception {
        productService.uploadProductDetailsForClient(clientId, file);
    }

    @PostMapping("/products/{clientId}/uploadjson")
    public ResponseEntity<List<Product>> uploadProduct(@PathVariable("clientId") Long clientId,
                                                       @RequestBody List<ProductDto> productDtoList) throws Exception {
        List<Product> result = productService.uploadProductDetailsForClientList(clientId, productDtoList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

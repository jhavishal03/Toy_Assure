package com.increff.controller;

import com.increff.Dto.ProductDto;
import com.increff.Model.ProductForm;
import com.increff.Model.Response;
import com.increff.Pojo.Product;
import com.increff.Service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api
@RequestMapping("/api")
public class ProductController {
    
    private ProductService productService;
    
    @Autowired
    private ProductDto productDto;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @ApiOperation(value = "Api to upload product details by CSV file")
    @PostMapping("/products/{clientId}/upload-product-details")
    public ResponseEntity<Response> uploadProductDetails(@PathVariable("clientId") Long clientId,
                                                         @RequestBody MultipartFile file) throws Exception {
        List<Product> products = productDto.uploadProductDetails(clientId, file);
        Response response = new Response<>("Product data uploaded successfully", products);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping("/products/{clientId}/upload-product-details-json")
    @ApiOperation(value = "Api to upload Product details")
    public ResponseEntity<Response> uploadProduct(@PathVariable("clientId") Long clientId,
                                                  @RequestBody @Valid List<ProductForm> productDtoList) throws Exception {
        List<Product> result = productService.uploadProductDetailsForClientList(clientId, productDtoList);
        return new ResponseEntity<>(new Response("product data uploaded", result), HttpStatus.OK);
    }
}

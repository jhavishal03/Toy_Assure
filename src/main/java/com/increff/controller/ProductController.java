package com.increff.controller;

import com.increff.Dto.ProductDto;
import com.increff.Dto.Response;
import com.increff.Model.Product;
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
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @ApiOperation(value = "Api to upload product details by CSV file")
    @PostMapping("/products/{clientId}/upload")
    public ResponseEntity<Response> uploadProductDetails(@PathVariable("clientId") Long clientId,
                                                         @RequestBody MultipartFile file) throws Exception {
        List<Product> products = productService.uploadProductDetailsForClient(clientId, file);
        Response response = new Response<>("Product data uploaded successfully", products);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping("/products/{clientId}/uploadjson")
    @ApiOperation(value = "Api to upload Product details")
    public ResponseEntity<Response> uploadProduct(@PathVariable("clientId") Long clientId,
                                                  @RequestBody @Valid List<ProductDto> productDtoList) throws Exception {
        List<Product> result = productService.uploadProductDetailsForClientList(clientId, productDtoList);
        return new ResponseEntity<>(new Response("product data uploaded", result), HttpStatus.OK);
    }
}

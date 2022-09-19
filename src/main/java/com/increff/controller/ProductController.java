package com.increff.controller;

import com.increff.Dto.ProductDto;
import com.increff.Model.Response;
import com.increff.Pojo.ProductPojo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Api
@RequestMapping("/api")
public class ProductController {
    //TODO remove response entity
    
    @Autowired
    private ProductDto productDto;
    
    
    @ApiOperation(value = "Api to upload product details by CSV file")
    @PostMapping("/products/{clientId}/upload-product-details")
    public ResponseEntity<Response> uploadProductDetails(@PathVariable("clientId") Long clientId,
                                                         @RequestBody MultipartFile file) throws Exception {
        List<ProductPojo> productPojos = productDto.uploadProductDetails(clientId, file);
        Response response = new Response<>("ProductPojo data uploaded successfully", productPojos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
}

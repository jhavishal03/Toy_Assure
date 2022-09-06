package com.increff.Dto;

import com.increff.Exception.ApiGenericException;
import com.increff.Exception.CSVFileParsingException;
import com.increff.Model.Converter.ProductConverter;
import com.increff.Model.ProductForm;
import com.increff.Pojo.Product;
import com.increff.Service.ProductService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductDto {
    
    @Autowired
    private ProductConverter productConverter;
    @Autowired
    private ProductService productService;
    
    public List<Product> uploadProductDetails(Long clientId, MultipartFile file) {
        List<ProductForm> productForms = null;
        try {
            productForms = new CsvToBeanBuilder(new InputStreamReader(new ByteArrayInputStream(file.getBytes()), "UTF8"))
                    .withType(ProductForm.class).withIgnoreEmptyLine(true).withSkipLines(1).build().parse();
        } catch (Exception e) {
            throw new CSVFileParsingException(e.getMessage());
        }
        Set<String> skuIds = new HashSet<>();
        Set<String> duplicateIds = productForms.stream().map(product -> product.getClientSkuId()).
                filter(ele -> !skuIds.add(ele)).collect(Collectors.toSet());
        if (duplicateIds.size() != 0) {
            throw new ApiGenericException("Duplicate Sku present in CSV file with sku-> " + duplicateIds);
            
        }
        List<Product> products = productConverter.productDtoToProductBulk(clientId, productForms);
        
        return productService.uploadProductDetailsForClient(clientId, products);
    }
    
}

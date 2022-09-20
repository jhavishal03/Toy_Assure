package com.increff.Dto;

import com.increff.Constants.UserType;
import com.increff.Model.Helper.ProductHelper;
import com.increff.Model.ProductForm;
import com.increff.Pojo.ProductPojo;
import com.increff.Service.Flow.ProductFlowApi;
import com.increff.Service.ProductApi;
import com.increff.Service.UserApi;
import com.increff.Util.CSVParseUtil;
import com.increff.common.Exception.ApiGenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductDto {
    
    
    private ProductApi productApi;
    
    private UserApi userApi;
    @Autowired
    private ProductFlowApi productFlowApi;
    
    @Autowired
    public ProductDto(ProductApi productApi, UserApi userApi) {
        this.productApi = productApi;
        this.userApi = userApi;
    }
    
    public List<ProductPojo> uploadProductDetails(Long clientId, MultipartFile file) throws IOException {
        List<ProductForm> productForms = this.parseProductCsv(file);
        List<ProductPojo> productPojos = ProductHelper.productFormToProductPojo(clientId, productForms);
        Optional.ofNullable(userApi.findUserById(clientId, UserType.CLIENT));
        return productFlowApi.updateProductsAndInventory(clientId, productPojos);
    }
    
    private List<ProductForm> parseProductCsv(MultipartFile file) {
        List<ProductForm> productForms = null;
        try {
            productForms = CSVParseUtil.parseCSV(file.getBytes(), ProductForm.class);
        } catch (IOException e) {
            throw new ApiGenericException("CSV IO exception while reading");
        }
        Set<String> skuIds = new HashSet<>();
        Set<String> duplicateIds = productForms.stream().map(product -> product.getClientSkuId()).
                filter(ele -> !skuIds.add(ele)).collect(Collectors.toSet());
        if (duplicateIds.size() != 0) {
            throw new ApiGenericException("Duplicate Sku present in CSV file with sku-> " + duplicateIds);
        }
        return productForms;
    }
    
}

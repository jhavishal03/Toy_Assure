package com.increff.Service;

import com.increff.Dao.ProductDao;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.Helper.ProductHelper;
import com.increff.Pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductApi {
    
    @Autowired
    private ProductDao productDao;
    
    @Autowired
    private ProductHelper productHelper;
    
    
    public List<ProductPojo> uploadProductDetailsForClient(Long clientId, List<ProductPojo> productPojos,
                                                           List<String> savedClientSkuIds) {
        
        return upsertClientSkuIds(clientId, savedClientSkuIds, productPojos);
    }
    
    
    public ProductPojo findProductByGlobalSkuID(Long globalSku) {
        ProductPojo productPojo = productDao.findProductByGlobalSkuId(globalSku);
        if (productPojo == null) {
            throw new ApiGenericException("ProductPojo doesn't Not exist with global Id-> " + globalSku);
        }
        return productPojo;
    }
    
    
    public List<String> getAllClientSkuIds(Long clientId) {
        List<String> res = productDao.getClientSkuIdByClientId(clientId);
        return res;
    }
    
    
    public ProductPojo findProductByClientIdAndSkuId(Long clientId, String clientSkuId) {
        ProductPojo productPojo = productDao.getProductByClientIdAndClientSkuId(clientId, clientSkuId);
        if (productPojo == null) {
            throw new ApiGenericException("ProductPojo not exist in system with SkuId " + clientSkuId
                    + " For current client");
        }
        return productPojo;
    }
    
    private List<ProductPojo> upsertClientSkuIds(Long clientId, List<String> savedClientSkuIds, List<ProductPojo> productPojos) {
        List<ProductPojo> result = new ArrayList<>();
        List<ProductPojo> productsToBeAdded = new ArrayList<>();
        List<ProductPojo> productsToBeUpdated = new ArrayList<>();
        //these productPojos need to be added
        productsToBeAdded = productPojos.stream().filter(prd -> !savedClientSkuIds.contains(prd.getClientSkuId()))
                .collect(Collectors.toList());
        result.addAll(productDao.addProductsData(productsToBeAdded));
        //these productPojos need to be updated
        productsToBeUpdated = productPojos.stream().filter(prd -> savedClientSkuIds.contains(prd.getClientSkuId())
        ).collect(Collectors.toList());
//        productDao.updateProductsDataForClient(clientId, productsToBeUpdated);
        result.addAll(updateGlobalIdForExistingProduct(clientId, productsToBeUpdated));
        return result;
    }
    
    private List<ProductPojo> updateGlobalIdForExistingProduct(Long clientId, List<ProductPojo> productsToBeUpdated) {
        List<ProductPojo> res = new ArrayList<>();
        for (ProductPojo productPojo : productsToBeUpdated) {
            ProductPojo savedProductPojo = productDao.getProductByClientIdAndClientSkuId(clientId, productPojo.getClientSkuId());
            savedProductPojo.setMrp(productPojo.getMrp());
            savedProductPojo.setDescription(productPojo.getDescription());
            savedProductPojo.setBrandId(productPojo.getBrandId());
            savedProductPojo.setName(productPojo.getName());
            res.add(savedProductPojo);
        }
        return res;
    }
    
}

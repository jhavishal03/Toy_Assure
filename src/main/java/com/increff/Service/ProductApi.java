package com.increff.Service;

import com.increff.Dao.ProductDao;
import com.increff.Pojo.ProductPojo;
import com.increff.common.Exception.ApiGenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductApi {
    @Autowired
    private ProductDao productDao;
    
    
    public ProductPojo findProductByGlobalSkuID(Long globalSku) {
        ProductPojo productPojo = productDao.findProductByGlobalSkuId(globalSku);
        if (productPojo == null) {
            throw new ApiGenericException("ProductPojo doesn't Not exist with global Id-> " + globalSku);
        }
        return productPojo;
    }
    
    public ProductPojo getProductByClientIdAndClientSku(Long clientId, String clientSkuId) {
        return productDao.getProductByClientIdAndClientSkuId(clientId, clientSkuId);
        
    }
    
    
    public Double findMrpByGlobalSkuId(Long globalSku) {
        return productDao.findMrpByGlobalSkuID(globalSku);
    }
    
    public ProductPojo findProductByClientIdAndSkuId(Long clientId, String clientSkuId) {
        ProductPojo productPojo = productDao.getProductByClientIdAndClientSkuId(clientId, clientSkuId);
        if (productPojo == null) {
            throw new ApiGenericException("ProductPojo not exist in system with SkuId " + clientSkuId
                    + " For current client");
        }
        return productPojo;
    }
    
    
    public List<ProductPojo> updateDetailsForExistingProduct(Long clientId, List<ProductPojo> productsToBeUpdated) {
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
    
    public List<ProductPojo> addProductsData(List<ProductPojo> productsToBeAdded) {
        return productDao.addProductsData(productsToBeAdded);
    }
}

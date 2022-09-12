package com.increff.Service.impl;

import com.increff.Dao.ProductDao;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.Helper.ProductHelper;
import com.increff.Model.ProductForm;
import com.increff.Pojo.Product;
import com.increff.Pojo.User;
import com.increff.Service.ProductService;
import com.increff.Service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductDao productDao;
    
    @Autowired
    private ProductHelper productHelper;
    
    @Override
    @Transactional
    public List<Product> uploadProductDetailsForClient(Long clientId, List<Product> products) {
        Optional<User> user = Optional.ofNullable(userService.findUserById(clientId));
        if (!user.isPresent()) {
            throw new ApiGenericException("Client not present with clientId" + clientId);
        }
        if (!user.get().getType().getValue().equals("Client")) {
            throw new ApiGenericException("Given user is not client");
        }
        
        List<String> savedClientSkuIds = productDao.getClientSkuIdByClientId(clientId);
        
        return upsertClientSkuIds(clientId, savedClientSkuIds, products);
    }
    
    @Override
    public List<Product> uploadProductDetailsForClientList(Long clientId, List<ProductForm> productDtoList) {
        
        Optional<User> savedUser = Optional.ofNullable(userService.findUserById(clientId));
        if (!savedUser.isPresent()) {
            throw new ApiGenericException("Client not present with clientId" + clientId);
        }
        if (!savedUser.get().getType().getValue().equals("Client")) {
            throw new ApiGenericException("Given user is not client");
        }
        List<String> savedClientSkuIds = productDao.getClientSkuIdByClientId(clientId);
        List<Product> products = productHelper.productDtoToProductBulk(clientId, productDtoList);
        return upsertClientSkuIds(clientId, savedClientSkuIds, products);
        
    }
    
    @Override
    public Product findProductByGlobalSkuID(Long globalSku) {
        Product product = productDao.findProductByGlobalSkuId(globalSku);
        if (product == null) {
            throw new ApiGenericException("Product doset Not exist with global Id-> " + globalSku);
        }
        return product;
    }
    
    @Override
    public List<Long> getAllGlobalSkuIds() {
        List<Long> res = productDao.getGlobalSkuIds();
        if (CollectionUtils.isEmpty(res)) {
            throw new ApiGenericException("No products exist in system");
        }
        return res;
    }
    
    @Override
    public Product findProductByClientIdAndSkuId(Long clientId, String clientSkuId) {
        Product product = productDao.getProductByClientIdAndClientSkuId(clientId, clientSkuId);
        if (product == null) {
            throw new ApiGenericException("Product not exist in system with SkuId " + clientSkuId
                    + " For current client");
        }
        return product;
    }
    
    private List<Product> upsertClientSkuIds(Long clientId, List<String> savedClientSkuIds, List<Product> products) {
        List<Product> result = new ArrayList<>();
        List<Product> productsToBeAdded = new ArrayList<>();
        List<Product> productsToBeUpdated = new ArrayList<>();
        //these products need to be added
        productsToBeAdded = products.stream().filter(prd -> !savedClientSkuIds.contains(prd.getClientSkuId()))
                .collect(Collectors.toList());
        result.addAll(productDao.addProductsData(productsToBeAdded));
        //these products need to be updated
        productsToBeUpdated = products.stream().filter(prd -> savedClientSkuIds.contains(prd.getClientSkuId())
        ).collect(Collectors.toList());
//        productDao.updateProductsDataForClient(clientId, productsToBeUpdated);
        result.addAll(updateGlobalIdForExistingProduct(clientId, productsToBeUpdated));
        return result;
    }
    
    private List<Product> updateGlobalIdForExistingProduct(Long clientId, List<Product> productsToBeUpdated) {
        List<Product> res = new ArrayList<>();
        for (Product product : productsToBeUpdated) {
            Product savedProduct = productDao.getProductByClientIdAndClientSkuId(clientId, product.getClientSkuId());
            savedProduct.setMrp(product.getMrp());
            savedProduct.setDescription(product.getDescription());
            savedProduct.setBrandId(product.getBrandId());
            savedProduct.setName(product.getName());
            res.add(savedProduct);
        }
        return res;
    }
    
}

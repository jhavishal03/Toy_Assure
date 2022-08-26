package com.increff.Service.impl;

import com.increff.Dao.ProductDao;
import com.increff.Dao.UserDao;
import com.increff.Dto.Converter.ProductConverter;
import com.increff.Dto.ProductDto;
import com.increff.Exception.ApiGenericException;
import com.increff.Exception.CSVFileParsingException;
import com.increff.Model.Product;
import com.increff.Model.User;
import com.increff.Service.ProductService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private ProductDao productDao;
    
    @Autowired
    private ProductConverter productConverter;
    
    @Override
    @Transactional
    public List<Product> uploadProductDetailsForClient(Long clientId, MultipartFile file) {
        Optional<User> user = Optional.ofNullable(userDao.findUserById(clientId));
        if (!user.isPresent()) {
            throw new ApiGenericException("Client not present with clientId" + clientId);
        }
        if (!user.get().getType().getValue().equals("Client")) {
            throw new ApiGenericException("Given user is not client");
        }
        List<ProductDto> products = null;
        try {
            products = new CsvToBeanBuilder(new InputStreamReader(new ByteArrayInputStream(file.getBytes()), "UTF8"))
                    .withType(ProductDto.class).withIgnoreEmptyLine(true).withSkipLines(1).build().parse();
        } catch (Exception e) {
            throw new CSVFileParsingException(e.getCause() + e.getMessage());
        }
        Set<String> skuIds = new HashSet<>();
        Set<String> duplicateIds = products.stream().map(product -> product.getClientSkuId()).
                filter(ele -> !skuIds.add(ele)).collect(Collectors.toSet());
        if (duplicateIds.size() != 0) {
            throw new ApiGenericException("Duplicate Sku present in CSV file with sku-> " + duplicateIds);
            
        }
        
        
        List<String> savedClientSkuIds = productDao.getClientSkuIdByClientId(clientId);
        
        return upsertClientSkuIds(clientId, savedClientSkuIds, products);
    }
    
    @Override
    public List<Product> uploadProductDetailsForClientList(Long clientId, List<ProductDto> productDtoList) {
        
        Optional<User> savedUser = Optional.ofNullable(userDao.findUserById(clientId));
        if (!savedUser.isPresent()) {
            throw new ApiGenericException("Client not present with clientId" + clientId);
        }
        if (!savedUser.get().getType().getValue().equals("Client")) {
            throw new ApiGenericException("Given user is not client");
        }
        List<String> savedClientSkuIds = productDao.getClientSkuIdByClientId(clientId);
        return upsertClientSkuIds(clientId, savedClientSkuIds, productDtoList);
        
    }
    
    private List<Product> upsertClientSkuIds(Long clientId, List<String> savedClientSkuIds, List<ProductDto> productDtoList) {
        List<Product> result = new ArrayList<>();
        List<Product> products = productConverter.productDtoToProductBulk(clientId, productDtoList);
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
            Product savedProduct = productDao.getProductForProductByClientIdAndClientSkuId(clientId, product.getClientSkuId());
            savedProduct.setMrp(product.getMrp());
            savedProduct.setDescription(product.getDescription());
            savedProduct.setBrandId(product.getBrandId());
            savedProduct.setName(product.getName());
//            productDao.updateProductsDataForClient(product);
            res.add(savedProduct);
        }
        return res;
    }
    
}

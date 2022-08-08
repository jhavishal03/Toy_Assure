package com.increff.Service.impl;

import com.increff.Dao.ProductDao;
import com.increff.Dao.UserDao;
import com.increff.Dto.Converter.ProductConverter;
import com.increff.Dto.ProductDto;
import com.increff.Exception.UserException;
import com.increff.Model.Product;
import com.increff.Model.User;
import com.increff.Service.ProductService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public List<Product> uploadProductDetailsForClient(Long clientId, MultipartFile file) throws IOException {

        List<ProductDto> products = null;
        try {
            List<Object> product = new CsvToBeanBuilder<>(new InputStreamReader(new ByteArrayInputStream(file.getBytes()), "UTF8"))
                    .withType(ProductDto.class).withSkipLines(1).build().parse();
            Optional<User> savedUser = Optional.ofNullable(userDao.findUserById(clientId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> savedClientSkuIds = productDao.getClientSkuIdByClientId(clientId);

        return upsertClientSkuIds(clientId, savedClientSkuIds, products);
    }

    @Override
    public List<Product> uploadProductDetailsForClientList(Long clientId, List<ProductDto> productDtoList) {

        Optional<User> savedUser = Optional.ofNullable(userDao.findUserById(clientId));
        if (!savedUser.isPresent()) {
            throw new UserException("Client not present with clientId" + clientId);
        }
        if (!savedUser.get().getType().getValue().equals("Client")) {
            throw new UserException("Given user is not client");
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
        productsToBeAdded = products.stream().filter(prd -> !savedClientSkuIds.contains(prd.getClientSkuId())).collect(Collectors.toList());
        result.addAll(productDao.addProductsData(productsToBeAdded));
        //these products need to be updated
        productsToBeUpdated = products.stream().filter(prd -> savedClientSkuIds.contains(prd.getClientSkuId())).collect(Collectors.toList());
//        productDao.updateProductsDataForClient(clientId, productsToBeUpdated);
        updateGlobalIdForExistingProduct(clientId, productsToBeUpdated);
        result.addAll(productsToBeUpdated);
        return result;


    }

    private void updateGlobalIdForExistingProduct(Long clientId, List<Product> productsToBeUpdated) {
        for (Product product : productsToBeUpdated) {
            Long id = productDao.getGlobalIdForProductByClientSkuIdAndClientId(clientId, product.getClientSkuId());
            product.setGlobalSkuId(id);
            productDao.updateProductsDataForClient(product);
        }

    }

}

package com.increff.Dao;

import com.increff.Model.Product;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao {
    
    private static String selectSkuByClientId = "select p.clientSkuId from Product p where clientId=:id";
    private static String getGlobalIdByClientSkuIdAndClientId =
            "Select p.globalSkuId from Product p where clientId=:id and clientSkuId=:skuId";
    
    private static String checkProductExistByClientSkuAndClientId = "select p from Product p where clientId=:id and clientSkuId=:skuId";
    private static String checkGlobalIdExistOrNot =
            "select globalSkuId from Product p";
    private static String findMrpByGlobalSkuId = "select mrp from Product where globalSkuId=:id";
    
    private static String findProductByGlobalSkuId = "select p from Product p where globalSkuId=:id";
    
    
    public List<String> getClientSkuIdByClientId(long id) {
        TypedQuery<String> query = getQuery(selectSkuByClientId, String.class);
        query.setParameter("id", id);
        return query.getResultList();
    }
    
    public Product findProductByGlobalSkuId(Long id) {
        TypedQuery<Product> query = getQuery(findProductByGlobalSkuId, Product.class);
        query.setParameter("id", id);
        return getSingle(query);
    }
    
    @Transactional
    public List<Product> addProductsData(List<Product> products) {
        List<Product> res = new ArrayList<>();
        for (Product product : products) {
            em.persist(product);
            res.add(product);
        }
        return res;
    }
    
    public Double findMrpByGlobalSkuID(Long id) {
        TypedQuery<Double> query = getQuery(findMrpByGlobalSkuId, Double.class);
        
        query.setParameter("id", id);
        return getSingle(query);
    }
    
    public List<Long> getGlobalSkuIds() {
        TypedQuery<Long> query = getQuery(checkGlobalIdExistOrNot, Long.class);
        return query.getResultList();
    }
    
    public Long getGlobalIdForProductByClientIdAndClientSkuId(Long id, String skuId) {
        TypedQuery<Long> query = getQuery(getGlobalIdByClientSkuIdAndClientId, Long.class);
        query.setParameter("id", id);
        query.setParameter("skuId", skuId);
        return getSingle(query);
    }
    
    public Product checkProductExistByClientIdAndClientSkuId(Long id, String skuId) {
        TypedQuery<Product> query = getQuery(checkProductExistByClientSkuAndClientId, Product.class);
        query.setParameter("id", id);
        query.setParameter("skuId", skuId);
        return query.getSingleResult();
    }
    
    @Transactional
    public Product updateProductsDataForClient(Product product) {
        em.merge(product);
        return product;
    }
    
}

package com.increff.Dao;

import com.increff.Pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao {
    
    private static String selectSkuByClientId = "select p.clientSkuId from ProductPojo p where clientId=:id";
    private static String getGlobalIdByClientSkuIdAndClientId =
            "Select p from ProductPojo p where clientId=:id and clientSkuId=:skuId";
    
    private static String checkProductExistByClientSkuAndClientId = "select p from ProductPojo p where clientId=:id " +
            "and clientSkuId=:skuId";
    private static String checkGlobalIdExistOrNot =
            "select globalSkuId from ProductPojo p";
    private static String findMrpByGlobalSkuId = "select mrp from ProductPojo where globalSkuId=:id";
    
    private static String findProductByGlobalSkuId = "select p from ProductPojo p where globalSkuId=:id";
    
    
    public List<String> getClientSkuIdByClientId(long id) {
        TypedQuery<String> query = getQuery(selectSkuByClientId, String.class);
        query.setParameter("id", id);
        return query.getResultList();
    }
    
    public ProductPojo findProductByGlobalSkuId(Long id) {
        TypedQuery<ProductPojo> query = getQuery(findProductByGlobalSkuId, ProductPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }
    
    @Transactional
    public List<ProductPojo> addProductsData(List<ProductPojo> productPojos) {
        List<ProductPojo> res = new ArrayList<>();
        for (ProductPojo productPojo : productPojos) {
            em.persist(productPojo);
            res.add(productPojo);
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
    
    public ProductPojo getProductByClientIdAndClientSkuId(Long id, String skuId) {
        TypedQuery<ProductPojo> query = getQuery(getGlobalIdByClientSkuIdAndClientId, ProductPojo.class);
        query.setParameter("id", id);
        query.setParameter("skuId", skuId);
        return getSingle(query);
    }
    
    public ProductPojo checkProductExistByClientIdAndClientSkuId(Long id, String skuId) {
        TypedQuery<ProductPojo> query = getQuery(checkProductExistByClientSkuAndClientId, ProductPojo.class);
        query.setParameter("id", id);
        query.setParameter("skuId", skuId);
        return getSingle(query);
    }
    
    @Transactional
    public ProductPojo updateProductsDataForClient(ProductPojo productPojo) {
        em.merge(productPojo);
        return productPojo;
    }
    
}

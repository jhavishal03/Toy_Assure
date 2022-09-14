package com.increff.Dao;

import com.increff.Pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Repository
public class InventoryDao extends AbstractDao {
    
    
    private static String getInventoryByGlobalSkuId = "select i from InventoryPojo i where globalSkuId=:skuId";
    
    public InventoryPojo getInvetoryBySkuId(Long skuId) {
        TypedQuery<InventoryPojo> query = getQuery(getInventoryByGlobalSkuId, InventoryPojo.class);
        query.setParameter("skuId", skuId);
        return getSingle(query);
    }
    
    @Transactional
    public void addInventoryEntity(InventoryPojo inventoryPojo) {
        em.persist(inventoryPojo);
        
    }
    
    @Transactional
    public InventoryPojo updateInventoryEntity(InventoryPojo inventoryPojo) {
        return em.merge(inventoryPojo);
    }
}

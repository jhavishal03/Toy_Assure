package com.increff.Dao;

import com.increff.Model.Inventory;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Repository
public class InventoryDao extends AbstractDao {
    
    
    private static String getInventoryByGlobalSkuId = "select i from Inventory i where globalSkuId=:skuId";
    
    public Inventory getInvetoryBySkuId(Long skuId) {
        TypedQuery<Inventory> query = getQuery(getInventoryByGlobalSkuId, Inventory.class);
        query.setParameter("skuId", skuId);
        return getSingle(query);
    }
    
    @Transactional
    public void addInventoryEntity(Inventory inventory) {
        em.persist(inventory);
        
    }
    
    @Transactional
    public Inventory updateInventoryEntity(Inventory inventory) {
        return em.merge(inventory);
    }
}

package com.increff.Service.impl;

import com.increff.Dao.InventoryDao;
import com.increff.Model.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class InventoryServiceImpl {
    @Autowired
    private InventoryDao inventoryDao;
    
    @Transactional
    public Inventory updateInventoryAfterFulfillment(Long globalSku, Long quantity) {
        Inventory inventory = inventoryDao.getInvetoryBySkuId(globalSku);
        inventory.setAllocatedQuantity(inventory.getAllocatedQuantity() - quantity);
        inventory.setFulfilledQuantity(inventory.getFulfilledQuantity() + quantity);
        return inventory;
    }
    
}

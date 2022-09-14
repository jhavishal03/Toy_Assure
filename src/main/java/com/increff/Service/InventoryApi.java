package com.increff.Service;

import com.increff.Dao.InventoryDao;
import com.increff.Pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class InventoryApi {
    @Autowired
    private InventoryDao inventoryDao;
    
    @Transactional
    public InventoryPojo updateInventoryAfterFulfillment(Long globalSku, Long quantity) {
        InventoryPojo inventoryPojo = inventoryDao.getInvetoryBySkuId(globalSku);
        inventoryPojo.setAllocatedQuantity(inventoryPojo.getAllocatedQuantity() - quantity);
        inventoryPojo.setFulfilledQuantity(inventoryPojo.getFulfilledQuantity() + quantity);
        return inventoryPojo;
    }
    
    public void addInventory(InventoryPojo inventoryPojo) {
        inventoryDao.addInventoryEntity(inventoryPojo);
    }
    
    public InventoryPojo getInventoryByGlobalSkuId(Long globalSkuId) {
        InventoryPojo inventoryPojo = inventoryDao.getInvetoryBySkuId(globalSkuId);
        return inventoryPojo;
    }
    
    public void updateInventoryAfterBinDataUpload(Long globalSku, Long changeInProductQuantity) {
        InventoryPojo inventoryPojo = inventoryDao.getInvetoryBySkuId(globalSku);
        if (inventoryPojo == null) {
            InventoryPojo inventoryPojoTobeAdded = new InventoryPojo();
            inventoryPojoTobeAdded.setGlobalSkuId(globalSku);
            inventoryPojoTobeAdded.setAvailableQuantity(changeInProductQuantity);
            inventoryDao.addInventoryEntity(inventoryPojoTobeAdded);
        } else {
            inventoryPojo.setAvailableQuantity(inventoryPojo.getAvailableQuantity() + changeInProductQuantity);
        }
    }
    
    public void addProductEntryInInventory(List<InventoryPojo> inventoryPojos) {
        for (InventoryPojo inventory : inventoryPojos) {
            this.addInventory(inventory);
        }
    }
}

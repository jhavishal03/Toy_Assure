package com.increff.Service.impl;

import com.increff.Dao.BinDao;
import com.increff.Dao.InventoryDao;
import com.increff.Dao.ProductDao;
import com.increff.Model.Converter.BinConverter;
import com.increff.Pojo.BinSku;
import com.increff.Pojo.Inventory;
import com.increff.Service.BinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class BinServiceImpl implements BinService {
    @Autowired
    private BinDao binDao;
    
    @Autowired
    private BinConverter binConverter;
    
    @Autowired
    private ProductDao productDao;
    
    @Autowired
    private InventoryDao inventoryDao;
    
    @Override
    public List<Long> addBinToSystem(int num) {
        return binDao.addBinToSystem(num);
    }
    
    @Override
    @Transactional
    public List<BinSku> uploadBinData(List<BinSku> binSkus) {
        List<Long> availableBins = binDao.getAllBinIds();
        List<Long> availableGlobalSkus = productDao.getGlobalSkuIds();
        Set<BinSku> entitiesToBeNewlyAdded = new HashSet<>();
        List<BinSku> result = new ArrayList<>();
        
        for (BinSku bin : binSkus) {
            Long changeInProductQuantity = 0L;
            if (!availableBins.contains(bin.getBinId()) || !availableGlobalSkus.contains(bin.getGlobalSkuId())) {
                continue;
            }
            Optional<BinSku> getSavedBin = Optional.ofNullable(binDao.getBinEntityByBinIdAndSkuId(bin.getGlobalSkuId(),
                    bin.getBinId()));
            
            if (!getSavedBin.isPresent()) {
                
                entitiesToBeNewlyAdded.add(bin);
                changeInProductQuantity = bin.getQuantity();
            } else {
                BinSku savedBin = getSavedBin.get();
                changeInProductQuantity = bin.getQuantity() - savedBin.getQuantity();
                savedBin.setQuantity(bin.getQuantity());
                result.add(savedBin);
            }
            //inventory update call to be made
            Inventory inventory = inventoryDao.getInvetoryBySkuId(bin.getGlobalSkuId());
            if (inventory == null) {
                Inventory inventoryTobeAdded = new Inventory();
                inventoryTobeAdded.setGlobalSkuId(bin.getGlobalSkuId());
                inventoryTobeAdded.setAvailableQuantity(changeInProductQuantity);
                inventoryDao.addInventoryEntity(inventoryTobeAdded);
            } else {
                inventory.setAvailableQuantity(inventory.getAvailableQuantity() + changeInProductQuantity);
                inventoryDao.updateInventoryEntity(inventory);
            }
        }
        
        result.addAll(binDao.uploadBinDataInventory(entitiesToBeNewlyAdded));
        return result;
    }
    
    //removing items from bin
    @Override
    public void removeProductsFromBinAfterAllocation(Long globalSkuId, Long quantityToBeRemoved) {
        List<BinSku> binSkus = binDao.getAllBinsContainingProductBySku(globalSkuId);
        Set<BinSku> binsToBeUpdated = new HashSet<>();
        for (BinSku bin : binSkus) {
            if (quantityToBeRemoved == 0) {
                break;
            }
            Long quantity = Math.min(quantityToBeRemoved, bin.getQuantity());
            quantityToBeRemoved -= quantity;
            bin.setQuantity(bin.getQuantity() - quantity);
            binsToBeUpdated.add(bin);
        }
    }
}

package com.increff.Service.impl;

import com.increff.Dao.BinDao;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.BinSkuDto;
import com.increff.Model.Converter.BinConverter;
import com.increff.Pojo.BinSku;
import com.increff.Pojo.Inventory;
import com.increff.Pojo.Product;
import com.increff.Service.BinService;
import com.increff.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BinServiceImpl implements BinService {
    @Autowired
    private BinDao binDao;
    
    @Autowired
    private BinConverter binConverter;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private InventoryServiceImpl inventoryService;
    
    @Override
    public List<Long> addBinToSystem(int num) {
        return binDao.addBinToSystem(num);
    }
    
    @Override
    @Transactional(rollbackOn = ApiGenericException.class)
    public List<BinSku> uploadBinData(Long clientId, List<BinSkuDto> binSkus) {
        List<Long> availableBins = binDao.getAllBinIds();
        Set<BinSku> entitiesToBeNewlyAdded = new HashSet<>();
        List<BinSku> result = new ArrayList<>();
        List<Long> binIds = binSkus.stream().map(bin -> bin.getBinId()).
                filter(bin -> !availableBins.contains(bin)).collect(Collectors.toList());
        if (binIds.size() != 0) {
            throw new ApiGenericException("Some of the Bins Not exist in system ", binIds);
        }
        for (BinSkuDto binDto : binSkus) {
            Long changeInProductQuantity = 0L;
            Product product = productService.findProductByClientIdAndSkuId(clientId, binDto.getClientSkuId());
            Long globalSku = product.getGlobalSkuId();
            
            Optional<BinSku> getSavedBin = Optional.ofNullable(binDao.getBinEntityByBinIdAndSkuId(globalSku,
                    binDto.getBinId()));
            //product is added first time in bin
            if (!getSavedBin.isPresent()) {
                BinSku bin = BinSku.builder().binId(binDto.getBinId())
                        .quantity(binDto.getQuantity())
                        .globalSkuId(globalSku).build();
                entitiesToBeNewlyAdded.add(bin);
                changeInProductQuantity = binDto.getQuantity();
            } else {
                BinSku savedBin = getSavedBin.get();
                changeInProductQuantity = binDto.getQuantity() - savedBin.getQuantity();
                savedBin.setQuantity(binDto.getQuantity());
                result.add(savedBin);
            }
            //inventory update call to be made
            Inventory inventory = inventoryService.getInventoryByGlobalSkuId(globalSku);
            if (inventory == null) {
                Inventory inventoryTobeAdded = new Inventory();
                inventoryTobeAdded.setGlobalSkuId(globalSku);
                inventoryTobeAdded.setAvailableQuantity(changeInProductQuantity);
                inventoryService.addInventory(inventoryTobeAdded);
            } else {
                inventory.setAvailableQuantity(inventory.getAvailableQuantity() + changeInProductQuantity);
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

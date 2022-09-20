package com.increff.Service;

import com.increff.Dao.BinDao;
import com.increff.Model.BinSkuForm;
import com.increff.Pojo.BinSkuPojo;
import com.increff.common.Exception.ApiGenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BinApi {
    @Autowired
    private BinDao binDao;
    
    
    public List<Long> addBinToSystem(Long num) {
        return binDao.addBinToSystem(num);
    }
    
    public List<Long> getBinIds(List<BinSkuForm> binSkus) {
        List<Long> availableBins = binDao.getAllBinIds();
        List<BinSkuPojo> result = new ArrayList<>();
        List<Long> binIds = binSkus.stream().map(bin -> bin.getBinId()).
                filter(bin -> !availableBins.contains(bin)).collect(Collectors.toList());
        if (binIds.size() != 0) {
            throw new ApiGenericException("Some of the Bins Not exist in system ", binIds);
        }
        return binIds;
    }
    
    public BinSkuPojo getBinSkuByBinIdandSkuId(Long globalSku, Long binId) {
        BinSkuPojo getSavedBin = binDao.getBinEntityByBinIdAndSkuId(globalSku,
                binId);
        return getSavedBin;
    }
    
    //    @Transactional
//    public List<BinSkuPojo> uploadBinData(Long clientId, List<BinSkuForm> binSkus) {
//        Set<BinSkuPojo> entitiesToBeNewlyAdded = new HashSet<>();
//        List<BinSkuPojo> result = new ArrayList<>();
//
//        for (BinSkuForm binDto : binSkus) {
//            Long changeInProductQuantity = 0L;
//            ProductPojo productPojo = productApi.findProductByClientIdAndSkuId(clientId, binDto.getClientSkuId());
//            Long globalSku = productPojo.getGlobalSkuId();
//
//            Optional<BinSkuPojo> getSavedBin = Optional.ofNullable(binDao.getBinEntityByBinIdAndSkuId(globalSku,
//                    binDto.getBinId()));
//            //productPojo is added first time in bin
//            if (!getSavedBin.isPresent()) {
//                BinSkuPojo bin = BinSkuPojo.builder().binId(binDto.getBinId())
//                        .quantity(binDto.getQuantity())
//                        .globalSkuId(globalSku).build();
//                entitiesToBeNewlyAdded.add(bin);
//                changeInProductQuantity = binDto.getQuantity();
//            } else {
//                BinSkuPojo savedBin = getSavedBin.get();
//                changeInProductQuantity = binDto.getQuantity() - savedBin.getQuantity();
//                savedBin.setQuantity(binDto.getQuantity());
//                result.add(savedBin);
//            }
//            //inventoryPojo update call to be made
//            InventoryPojo inventoryPojo = inventoryService.getInventoryByGlobalSkuId(globalSku);
//            if (inventoryPojo == null) {
//                InventoryPojo inventoryPojoTobeAdded = new InventoryPojo();
//                inventoryPojoTobeAdded.setGlobalSkuId(globalSku);
//                inventoryPojoTobeAdded.setAvailableQuantity(changeInProductQuantity);
//                inventoryService.addInventory(inventoryPojoTobeAdded);
//            } else {
//                inventoryPojo.setAvailableQuantity(inventoryPojo.getAvailableQuantity() + changeInProductQuantity);
//            }
//        }
//
//        result.addAll(binDao.uploadBinDataInventory(entitiesToBeNewlyAdded));
//        return result;
//    }
    public List<BinSkuPojo> addBinSkuDataList(Set<BinSkuPojo> binSkuPojos) {
        return binDao.uploadBinDataInventory(binSkuPojos);
    }
    
    //removing items from bin
    public void removeProductsFromBinAfterAllocation(Long globalSkuId, Long quantityToBeRemoved) {
        List<BinSkuPojo> binSkusPojos = binDao.getAllBinsContainingProductBySku(globalSkuId);
        Set<BinSkuPojo> binsToBeUpdated = new HashSet<>();
        for (BinSkuPojo bin : binSkusPojos) {
            if (quantityToBeRemoved == 0) {
                break;
            }
            Long quantity = Math.min(quantityToBeRemoved, bin.getQuantity());
            quantityToBeRemoved -= quantity;
            bin.setQuantity(bin.getQuantity() - quantity);
            binsToBeUpdated.add(bin);
        }
        if (quantityToBeRemoved > 0) {
            throw new ApiGenericException("Sufficient quantity of products not exist in Bins , Data Mismatch");
        }
    }
}

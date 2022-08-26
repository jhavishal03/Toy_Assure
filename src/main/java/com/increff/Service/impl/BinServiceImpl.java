package com.increff.Service.impl;

import com.increff.Dao.BinDao;
import com.increff.Dao.InventoryDao;
import com.increff.Dao.ProductDao;
import com.increff.Dto.BinSkuDto;
import com.increff.Dto.Converter.BinConverter;
import com.increff.Exception.ApiGenericException;
import com.increff.Exception.CSVFileParsingException;
import com.increff.Model.BinSku;
import com.increff.Model.Inventory;
import com.increff.Service.BinService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

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
    public void addBinToSystem(int num) {
        binDao.addBinToSystem(num);
    }
    
    @Override
    @Transactional
    public List<BinSku> uploadBinData(MultipartFile binDataCsv) {
        List<BinSkuDto> binData = null;
        List<Long> availableBins = binDao.getAllBinIds();
        List<Long> availableGlobalSkus = productDao.getGlobalSkuIds();
        Set<BinSku> entitiesToBeNewlyAdded = new HashSet<>();
        Set<BinSku> entitiesToBeUpdated = new HashSet<>();
        try {
            binData = new CsvToBeanBuilder(new InputStreamReader(new ByteArrayInputStream(binDataCsv.getBytes())))
                    .withType(BinSkuDto.class).withSkipLines(1).build().parse();
        } catch (Exception e) {
            throw new CSVFileParsingException(e.getCause() + e.getMessage());
        }
        Set<Long> skuIds = binData.stream().map(binSku -> binSku.getGlobalSkuId()).collect(Collectors.toSet());
        if (Integer.compare(binData.size(), skuIds.size()) != 0) {
            throw new ApiGenericException("Duplicate Sku present in CSV file");
        }
        
        
        List<BinSku> binSkus = binConverter.convertBinSkuDtoToBin(binData);
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
//            Long currentProductQuantity = getSavedBin.get().getQuantity();
//            bin.setQuantity(bin.getQuantity() + currentProductQuantity);
                changeInProductQuantity = bin.getQuantity() - savedBin.getQuantity();
                savedBin.setQuantity(bin.getQuantity());
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
//        result.addAll(binDao.updateBinDataInventory(entitiesToBeUpdated));
        
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
        binDao.uploadBinDataInventory(binsToBeUpdated);
    }
}

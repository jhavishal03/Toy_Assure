package com.increff.Dto;

import com.increff.Exception.ApiGenericException;
import com.increff.Model.BinSkuForm;
import com.increff.Model.Helper.BinHelper;
import com.increff.Pojo.BinSkuPojo;
import com.increff.Pojo.ProductPojo;
import com.increff.Service.BinApi;
import com.increff.Service.InventoryApi;
import com.increff.Service.ProductApi;
import com.increff.Util.CSVParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.increff.Model.Helper.DtoHelper.check;


@Service
public class BinDto {
    
    ProductApi productApi;
    
    private InventoryApi inventoryApi;
    
    private BinApi binApi;
    
    private BinHelper binConverter;
    
    @Autowired
    public BinDto(ProductApi productApi, InventoryApi inventoryApi,
                  BinApi binApi, BinHelper binConverter) {
        this.productApi = productApi;
        this.inventoryApi = inventoryApi;
        this.binApi = binApi;
        this.binConverter = binConverter;
    }
    
    public List<Long> addBinsToSystem(Long num) {
        check(num, "BinQuantity ");
        return binApi.addBinToSystem(num);
    }
    
    @Transactional
    public List<BinSkuPojo> uploadBinData(Long clientId, MultipartFile file) {
        List<BinSkuForm> binData = this.parseCSV(file);
        List<Long> binIds = binApi.getBinIds(binData);
        return binApi.addBinSkuDataList(this.upsertBinData(clientId, binData));
//        return binApi.uploadBinData(clientId, binData);
    }
    
    private List<BinSkuForm> parseCSV(MultipartFile file) {
        List<BinSkuForm> binData = null;
        
        try {
            binData = CSVParseUtil.parseCSV(file.getBytes(), BinSkuForm.class);
        } catch (IOException e) {
            throw new ApiGenericException("CSV IO exception while reading");
        }
        Set<String> skuIds = new HashSet<>();
        Set<String> duplicateSkuIds = binData.stream().map(binSku -> binSku.getClientSkuId()).filter(ele -> !skuIds.add(ele))
                .collect(Collectors.toSet());
        if (duplicateSkuIds.size() != 0) {
            throw new ApiGenericException("Duplicate Sku present in CSV file with Ids -> " + duplicateSkuIds);
        }
        return binData;
    }
    
    
    private Set<BinSkuPojo> upsertBinData(Long clientId, List<BinSkuForm> binSkus) {
        Set<BinSkuPojo> entitiesToBeNewlyAdded = new HashSet<>();
        List<BinSkuPojo> result = new ArrayList<>();
        
        for (BinSkuForm binDto : binSkus) {
            Long changeInProductQuantity = 0L;
            ProductPojo productPojo = productApi.findProductByClientIdAndSkuId(clientId, binDto.getClientSkuId());
            Long globalSku = productPojo.getGlobalSkuId();
            
            Optional<BinSkuPojo> getSavedBin = Optional.ofNullable(binApi.getBinSkuByBinIdandSkuId(globalSku,
                    binDto.getBinId()));
            //productPojo is added first time in bin
            if (!getSavedBin.isPresent()) {
                BinSkuPojo bin = BinSkuPojo.builder().binId(binDto.getBinId())
                        .quantity(binDto.getQuantity())
                        .globalSkuId(globalSku).build();
                entitiesToBeNewlyAdded.add(bin);
                changeInProductQuantity = binDto.getQuantity();
            } else {
                BinSkuPojo savedBin = getSavedBin.get();
                changeInProductQuantity = binDto.getQuantity() - savedBin.getQuantity();
                savedBin.setQuantity(binDto.getQuantity());
                result.add(savedBin);
            }
            //inventoryPojo update call to be made
            inventoryApi.updateInventoryAfterBinDataUpload(globalSku, changeInProductQuantity);
        }
        return entitiesToBeNewlyAdded;
    }
}

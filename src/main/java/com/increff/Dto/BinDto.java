package com.increff.Dto;

import com.increff.Model.BinSkuForm;
import com.increff.Model.Helper.DtoHelper;
import com.increff.Pojo.BinSkuPojo;
import com.increff.Service.BinApi;
import com.increff.Service.Flow.BinFlowApi;
import com.increff.Service.ProductApi;
import com.increff.Util.CSVParseUtil;
import com.increff.common.Exception.ApiGenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.increff.Model.Helper.DtoHelper.check;


@Service
public class BinDto {
    
    ProductApi productApi;
    
    
    private BinApi binApi;
    
    private BinFlowApi binFlowApi;
    
    @Autowired
    public BinDto(ProductApi productApi, BinApi binApi, BinFlowApi binFlowApi) {
        this.productApi = productApi;
        this.binApi = binApi;
        this.binFlowApi = binFlowApi;
    }
    
    public List<Long> addBinsToSystem(Long num) {
        check(num, "BinQuantity ");
        return binApi.addBinToSystem(num);
    }
    
    public List<BinSkuPojo> uploadBinData(Long clientId, MultipartFile file) {
        DtoHelper.check(clientId, " clientId");
        List<BinSkuForm> binData = this.parseCSV(file);
        return binFlowApi.updateBinAndInventoryForBinUpload(clientId, binData);
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
    
    
}

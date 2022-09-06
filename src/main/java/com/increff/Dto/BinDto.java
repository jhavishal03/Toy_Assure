package com.increff.Dto;

import com.increff.Exception.ApiGenericException;
import com.increff.Exception.CSVFileParsingException;
import com.increff.Model.BinSkuDto;
import com.increff.Model.Converter.BinConverter;
import com.increff.Pojo.BinSku;
import com.increff.Service.BinService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BinDto {
    @Autowired
    private BinService binService;
    
    @Autowired
    private BinConverter binConverter;
    
    public List<Long> addBinsToSystem(int num) {
        return binService.addBinToSystem(num);
    }
    
    public List<BinSku> uploadBinData(MultipartFile file) {
        List<BinSkuDto> binData = null;
        try {
            binData = new CsvToBeanBuilder(new InputStreamReader(new ByteArrayInputStream(file.getBytes())))
                    .withType(BinSkuDto.class).withSkipLines(1).build().parse();
        } catch (Exception e) {
            throw new CSVFileParsingException(e.getCause() + e.getMessage());
        }
        Set<Long> skuIds = binData.stream().map(binSku -> binSku.getGlobalSkuId()).collect(Collectors.toSet());
        if (Integer.compare(binData.size(), skuIds.size()) != 0) {
            throw new ApiGenericException("Duplicate Sku present in CSV file");
        }
        List<BinSku> binSkus = binConverter.convertBinSkuDtoToBin(binData);
        
        return binService.uploadBinData(binSkus);
    }
}

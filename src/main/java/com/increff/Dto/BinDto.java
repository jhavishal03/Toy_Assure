package com.increff.Dto;

import com.increff.Exception.ApiGenericException;
import com.increff.Model.BinSkuDto;
import com.increff.Model.Converter.BinConverter;
import com.increff.Pojo.BinSku;
import com.increff.Service.BinService;
import com.increff.Util.CSVParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
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
    
    public List<BinSku> uploadBinData(Long clientId, MultipartFile file) {
        List<BinSkuDto> binData = null;
        try {
            binData = CSVParseUtil.parseCSV(file.getBytes(), BinSkuDto.class);

//             binData = new CsvToBeanBuilder(new InputStreamReader(new ByteArrayInputStream(file.getBytes())))
//                    .withType(BinSkuDto.class).withSkipLines(1).withThrowExceptions(false).build().parse();
//
        } catch (IOException e) {
            throw new ApiGenericException("CSV IO exception while reading");
        }
        Set<String> skuIds = new HashSet<>();
        Set<String> duplicateSkuIds = binData.stream().map(binSku -> binSku.getClientSkuId()).filter(ele -> !skuIds.add(ele))
                .collect(Collectors.toSet());
        if (duplicateSkuIds.size() != 0) {
            throw new ApiGenericException("Duplicate Sku present in CSV file with Ids -> " + duplicateSkuIds);
        }
//        PropertyValidator.validateBinSkuList(binData);
//        List<BinSku> binSkus = binConverter.convertBinSkuDtoToBin(binData);
        
        return binService.uploadBinData(clientId, binData);
    }
}

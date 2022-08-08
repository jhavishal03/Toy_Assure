package com.increff.Service.impl;

import com.increff.Dao.BinDao;
import com.increff.Dao.ProductDao;
import com.increff.Dto.BinSkuDto;
import com.increff.Dto.Converter.BinConverter;
import com.increff.Model.BinSku;
import com.increff.Service.BinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BinServiceImpl implements BinService {
    @Autowired
    private BinDao binDao;

    @Autowired
    private BinConverter binConverter;

    @Autowired
    private ProductDao productDao;

    @Override
    public void addBinToSystem(int num) {
        binDao.addBinToSystem(num);
    }

    @Override
    public List<BinSku> uploadBinData(List<BinSkuDto> binData) {
        List<Long> avaialbleBins = binDao.getAllBinIds();
        List<Long> availableGlobalSkus = productDao.getGlobalSkuIds();
        Set<BinSku> entitiesToBeNewlyAdded = new HashSet<>();
        Set<BinSku> entitiesToBeUpdated = new HashSet<>();
        List<BinSku> binSkus = binConverter.convertBinSkuDtoToBin(binData);
        List<BinSku> result = new ArrayList<>();

        for (BinSku bin : binSkus) {
            if (!avaialbleBins.contains(bin.getBinId()) || !availableGlobalSkus.contains(bin.getGlobalSkuId())) {
                continue;
            }
            Optional<BinSku> getSavedBin = Optional.ofNullable(binDao.getBinEntityByBinIdAndSkuId(bin.getGlobalSkuId(), bin.getBinId()));

            if (!getSavedBin.isPresent()) {
                entitiesToBeNewlyAdded.add(bin);
            } else {
                bin.setBinSkuId(getSavedBin.get().getBinSkuId());

//            Long currentProductQuantity = getSavedBin.get().getQuantity();
//            bin.setQuantity(bin.getQuantity() + currentProductQuantity);
                bin.setQuantity(getSavedBin.get().getQuantity());
                entitiesToBeUpdated.add(bin);
                //inventory update call to be made
            }
        }

        result.addAll(binDao.uploadBinDataInventory(entitiesToBeNewlyAdded));
        result.addAll(binDao.updateBinDataInventory(entitiesToBeUpdated));

        return result;
    }
}

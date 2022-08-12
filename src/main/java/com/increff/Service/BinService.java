package com.increff.Service;

import com.increff.Model.BinSku;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BinService {

    public void addBinToSystem(int num);

    public List<BinSku> uploadBinData(MultipartFile binData);

    public void removeProductsFromBinAfterAllocation(Long globalSkuId, Long quantity);

}

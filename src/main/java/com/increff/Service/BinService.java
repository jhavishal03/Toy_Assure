package com.increff.Service;

import com.increff.Dto.BinSkuDto;
import com.increff.Model.BinSku;

import java.util.List;

public interface BinService {

    public void addBinToSystem(int num);

    public List<BinSku> uploadBinData(List<BinSkuDto> binData);
}

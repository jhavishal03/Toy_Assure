package com.increff.controller;

import com.increff.Dto.BinSkuDto;
import com.increff.Model.BinSku;
import com.increff.Service.BinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BinController {
    @Autowired
    private BinService binService;

    @PostMapping("/bins")
    public void addBins(@RequestParam int numBin) {
        binService.addBinToSystem(numBin);
    }

    @PutMapping("/updateBin")
    public ResponseEntity<List<BinSku>> uploadBinWiseInventory(@RequestBody List<BinSkuDto> binData) {
        List<BinSku> updatedData = binService.uploadBinData(binData);
        return new ResponseEntity<>(updatedData, HttpStatus.OK);
    }
}

package com.increff.controller;

import com.increff.Model.BinSku;
import com.increff.Service.BinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Api
@RequestMapping("/api")
public class BinController {
    @Autowired
    private BinService binService;

    @PostMapping("/bins")
    @ApiOperation(value = "Api to add Num of bins to the system")
    public void addBins(@RequestParam int numBin) {
        binService.addBinToSystem(numBin);
    }

    @ApiOperation(value = "This Api is used to update quantity of product in a Bin")
    @PutMapping("/updateBin")
    public ResponseEntity<List<BinSku>> uploadBinWiseInventory(@RequestBody MultipartFile binData) {
        List<BinSku> updatedData = binService.uploadBinData(binData);
        return new ResponseEntity<>(updatedData, HttpStatus.OK);
    }
}

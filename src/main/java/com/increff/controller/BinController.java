package com.increff.controller;

import com.increff.Dto.Response;
import com.increff.Model.BinSku;
import com.increff.Service.BinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<Response> addBins(@RequestParam int numBin) {
        binService.addBinToSystem(numBin);
        return new ResponseEntity(new Response<>("Bins Added Succcessfully", ""), HttpStatus.CREATED);
    }
    
    @ApiOperation(value = "This Api is used to update quantity of product in a Bin")
    @PostMapping("/updateBin")
    public ResponseEntity<Response> uploadBinWiseInventory(@RequestParam("file") MultipartFile file) {
        List<BinSku> updatedData = binService.uploadBinData(file);
        return new ResponseEntity<>(
                new Response("product quantity updated in Bins", updatedData), HttpStatus.OK);
    }
}

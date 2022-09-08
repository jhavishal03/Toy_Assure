package com.increff.controller;

import com.increff.Dto.BinDto;
import com.increff.Model.Response;
import com.increff.Pojo.BinSku;
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
    private BinDto binDto;
    
    @PostMapping("/create-bins")
    @ApiOperation(value = "Api to add Num of bins to the system")
    public ResponseEntity<Response> addBins(@RequestParam int numBin) {
        List<Long> res = binDto.addBinsToSystem(numBin);
        return new ResponseEntity(new Response<>("Bins Added Successfully with Bin Ids-> ", res), HttpStatus.CREATED);
    }
    
    @ApiOperation(value = "This Api is used to update quantity of product in a Bin")
    @PostMapping(value = "/bin/{client-Id}/upload-data")
    public ResponseEntity<Response> uploadBinWiseInventory(@PathVariable("client-Id") Long clientId,
                                                           @RequestBody MultipartFile file) {
        List<BinSku> updatedData = binDto.uploadBinData(clientId, file);
        return new ResponseEntity<>(
                new Response("product quantity updated in Bins", updatedData), HttpStatus.OK);
    }
}

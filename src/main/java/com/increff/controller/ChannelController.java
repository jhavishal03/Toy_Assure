package com.increff.controller;

import com.increff.Dto.ChannelDto;
import com.increff.Model.ChannelForm;
import com.increff.Model.Response;
import com.increff.Pojo.Channel;
import com.increff.Pojo.ChannelListing;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api
@RequestMapping("/api")
public class ChannelController {
    
    private ChannelDto channelDto;
    
    @Autowired
    public ChannelController(ChannelDto channelDto) {
        this.channelDto = channelDto;
    }
    
    @ApiOperation(value = "Api to Create Channel ")
    @PostMapping("/create-channel")
    public ResponseEntity<Response> createChannel(@RequestBody @Valid ChannelForm channelForm) {
        Channel channel = channelDto.addChannel(channelForm);
        Response response = new Response("Channel created succesfully", channel);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @ApiOperation(value = "Api to enter channel Listing details by csv File")
    @PostMapping("/add-channel-listings")
    public ResponseEntity<Response> createChannelListing(@RequestParam String channelName, @RequestParam String clientName,
                                                         @RequestBody MultipartFile channelListings) {
        List<ChannelListing> res = new ArrayList<>();
        res = channelDto.addChannelListingsDto(clientName, channelName, channelListings);
        Response response = new Response("Channel Listings updated successFully", res);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

package com.increff.controller;

import com.increff.Dto.ChannelDto;
import com.increff.Dto.Response;
import com.increff.Model.Channel;
import com.increff.Model.ChannelListing;
import com.increff.Service.ChannelService;
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
    
    private ChannelService channelService;
    
    @Autowired
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }
    
    @ApiOperation(value = "Api to Create Channel ")
    @PostMapping("/channel")
    public ResponseEntity<Response> createChannel(@RequestBody @Valid ChannelDto channelDto) {
        Channel channel = channelService.addChannel(channelDto);
        Response response = new Response("Channel created succesfully", channel);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @ApiOperation(value = "Api to enter channel Listing details by csv File")
    @PostMapping("/channelListing")
    public ResponseEntity<Response> createChannelListing(@RequestParam String channelName, @RequestParam String clientName,
                                                         @RequestBody MultipartFile channelListings) {
        List<ChannelListing> res = new ArrayList<>();
        channelService.addChannelListings(clientName, channelName, channelListings);
        Response response = new Response("Channel Listings updated successFully", res);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

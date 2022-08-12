package com.increff.controller;

import com.increff.Dto.ChannelDto;
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
    public ResponseEntity<Channel> createChannel(@RequestBody @Valid ChannelDto channelDto) {
        Channel channel = channelService.addChannel(channelDto);
        return new ResponseEntity<>(channel, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Api to enter channel Listing details by csv File")
    @PostMapping("/channelListing")
    public ResponseEntity<List<ChannelListing>> createChannelListing(@RequestParam String channelName, @RequestParam String clientName,
                                                                     @RequestBody MultipartFile channelListings) {
        List<ChannelListing> res = new ArrayList<>();
        channelService.addChannelListings(clientName, channelName, channelListings);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}

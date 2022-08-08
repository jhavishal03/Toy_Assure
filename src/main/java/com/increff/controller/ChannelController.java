package com.increff.controller;

import com.increff.Dto.ChannelDto;
import com.increff.Model.Channel;
import com.increff.Service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChannelController {

    private ChannelService channelService;

    @Autowired
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping("channel")
    public ResponseEntity<Channel> createChannel(@RequestBody ChannelDto channelDto) {
        Channel channel = channelService.addChannel(channelDto);
        return new ResponseEntity<>(channel, HttpStatus.CREATED);
    }
}

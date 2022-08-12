package com.increff.Service;

import com.increff.Dto.ChannelDto;
import com.increff.Model.Channel;
import com.increff.Model.ChannelListing;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChannelService {
    public Channel addChannel(ChannelDto channelDto);

    public List<ChannelListing> addChannelListings(String clientName, String channelName, MultipartFile file);
}

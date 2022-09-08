package com.increff.Service;

import com.increff.Constants.InvoiceType;
import com.increff.Model.ChannelListingCsv;
import com.increff.Pojo.Channel;
import com.increff.Pojo.ChannelListing;

import java.util.List;

public interface ChannelService {
    public Channel addChannel(Channel channel);
    
    public List<ChannelListing> addChannelListings(String clientName, String channelName, List<ChannelListingCsv> channels);
    
    public Channel getChannelByChannelName(String channelName);
    
    public Channel getChannelByNameAndType(String name, InvoiceType type);
    
    
    public Long getGlobalSkuIDByClientIdAndChannelIdAndSkuId(Long clientId, Long channelId, String skuId);
}

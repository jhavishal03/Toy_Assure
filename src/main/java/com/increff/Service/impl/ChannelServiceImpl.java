package com.increff.Service.impl;

import com.increff.Constants.InvoiceType;
import com.increff.Constants.UserType;
import com.increff.Dao.ChannelDao;
import com.increff.Dao.ProductDao;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.ChannelListingCsv;
import com.increff.Pojo.Channel;
import com.increff.Pojo.ChannelListing;
import com.increff.Pojo.Product;
import com.increff.Pojo.User;
import com.increff.Service.ChannelService;
import com.increff.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChannelServiceImpl implements ChannelService {
    
    
    private ChannelDao channelDao;
    
    private UserService userService;
    
    private ProductDao productDao;
    
    @Autowired
    public ChannelServiceImpl(ChannelDao channelDao, UserService userService, ProductDao productDao) {
        
        this.channelDao = channelDao;
        this.userService = userService;
        this.productDao = productDao;
    }
    
    
    @Override
    public Channel addChannel(Channel channel) {
        Optional<Channel> isExistChannel = channelDao.checkChannelExistOrNot(channel.getName());
        if (isExistChannel.isPresent() && isExistChannel.get().getName().equalsIgnoreCase(channel.getName())) {
            throw new ApiGenericException("Channel Already Exist with channelName -> " + channel.getName());
        }
        return channelDao.saveChannel(channel);
    }
    
    @Override
    @Transactional(rollbackOn = ApiGenericException.class)
    public List<ChannelListing> addChannelListings(String clientName, String channelName, List<ChannelListingCsv> channels) {
        
        List<ChannelListing> channelListingsList = new ArrayList<>();
        Optional<User> savedUser = userService.getUserByNameAndType(clientName, UserType.CLIENT);
        if (!savedUser.isPresent()) {
            throw new ApiGenericException("Client doesn't exist");
        }
        Optional<Channel> savedChannel = channelDao.checkChannelExistOrNot(channelName);
        if (!savedChannel.isPresent()) {
            throw new ApiGenericException("Channel doesnot exist");
        }
        Long clientId = savedUser.get().getUserId();
        Long channelId = savedChannel.get().getChannelId();
        for (ChannelListingCsv channel : channels) {
            Long channelSkuIdPresnt = this.getGlobalSkuIDByClientIdAndChannelIdAndSkuId(clientId, channelId, channel.getChannelSkuId());
            if (channelSkuIdPresnt != null) {
                throw new ApiGenericException("ChannelSkuId " + channel.getChannelSkuId() + " already in use for client " + clientName);
            }
            Product product = productDao.checkProductExistByClientIdAndClientSkuId(clientId,
                    channel.getClientSkuId());
            if (product == null) {
                throw new ApiGenericException("Product not exist for client " + clientName + " SkuId " + channel.getClientSkuId());
            }
            // if else extra lagana hai
            ChannelListing savedChannelListing =
                    channelDao.findChannelListingBySkuIDByChannelIdAndSkuId(clientId, channelId, product.getGlobalSkuId(),
                            channel.getChannelSkuId());
            if (savedChannelListing == null) {
                Product savedProduct = productDao.getProductByClientIdAndClientSkuId(
                        savedUser.get().getUserId(), channel.getClientSkuId());
                ChannelListing obj = ChannelListing.builder().channelId(savedChannel.get().getChannelId())
                        .clientId(savedUser.get().getUserId()).channelSkuId(channel.getChannelSkuId())
                        .globalSkuId(savedProduct.getGlobalSkuId()).build();
                channelDao.addSingleChannelListing(obj);
                channelListingsList.add(obj);
            }
//            else {
//                savedChannelListing.setChannelSkuId(channel.getChannelSkuId());
//                channelListingsList.add(savedChannelListing);
//            }
        }
        return channelListingsList;
    }
    
    @Override
    public Channel getChannelByChannelName(String channelName) {
        Optional<Channel> channel = channelDao.checkChannelExistOrNot(channelName);
        if (!channel.isPresent()) {
            throw new ApiGenericException("channel doesn't exist");
        }
        return channel.get();
    }
    
    @Override
    public Channel getChannelByNameAndType(String name, InvoiceType type) {
        Channel channel = channelDao.getChannelIdByNameAndType(name, type);
        if (channel == null) {
            throw new ApiGenericException("channel Not exist");
        }
        return channel;
    }
    
    @Override
    public Long getGlobalSkuIDByClientIdAndChannelIdAndSkuId(Long clientId, Long channelId, String channelSkuId) {
        return channelDao.getGlobalSkuIDByChannelIdAndSkuId(clientId, channelId, channelSkuId);
    }
}

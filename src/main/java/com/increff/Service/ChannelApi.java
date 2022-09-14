package com.increff.Service;

import com.increff.Constants.InvoiceType;
import com.increff.Dao.ChannelDao;
import com.increff.Exception.ApiGenericException;
import com.increff.Pojo.ChannelListingPojo;
import com.increff.Pojo.ChannelPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ChannelApi {
    
    @Autowired
    private ChannelDao channelDao;
    
    @Transactional
    public ChannelPojo addChannel(ChannelPojo channelPojo) {
        Optional<ChannelPojo> isExistChannel = channelDao.checkChannelExistOrNot(channelPojo.getName());
        if (isExistChannel.isPresent() && isExistChannel.get().getName().equalsIgnoreCase(channelPojo.getName())) {
            throw new ApiGenericException("ChannelPojo Already Exist with channelName -> " + channelPojo.getName());
        }
        return channelDao.saveChannel(channelPojo);
    }
    
    public ChannelListingPojo addChannelListing(ChannelListingPojo channelListingPojo) {
        return channelDao.addSingleChannelListing(channelListingPojo);
    }
//    public List<ChannelListingPojo> addChannelListings(Long clientId, String channelName, List<ChannelListingCsvForm> channels) {
//
//        List<ChannelListingPojo> channelListingsListPojo = new ArrayList<>();
//
//        Optional<ChannelPojo> savedChannel = channelDao.checkChannelExistOrNot(channelName);
//        if (!savedChannel.isPresent()) {
//            throw new ApiGenericException("ChannelPojo doesnot exist");
//        }
//        Long channelId = savedChannel.get().getChannelId();
//        for (ChannelListingCsvForm channel : channels) {
//            Long channelSkuIdPresnt = this.getGlobalSkuIDByClientIdAndChannelIdAndSkuId(clientId, channelId, channel.getChannelSkuId());
//            if (channelSkuIdPresnt != null) {
//                throw new ApiGenericException("ChannelSkuId " + channel.getChannelSkuId() + " already in use for client " + clientId);
//            }
//            ProductPojo productPojo = productDao.checkProductExistByClientIdAndClientSkuId(clientId,
//                    channel.getClientSkuId());
//            if (productPojo == null) {
//                throw new ApiGenericException("ProductPojo not exist for client " + clientId + " SkuId " + channel.getClientSkuId());
//            }
//            // if else extra lagana hai
//            ChannelListingPojo savedChannelListingPojo =
//                    channelDao.findChannelListingBySkuIDByChannelIdAndSkuId(clientId, channelId, productPojo.getGlobalSkuId(),
//                            channel.getChannelSkuId());
//            if (savedChannelListingPojo == null) {
//                ProductPojo savedProductPojo = productDao.getProductByClientIdAndClientSkuId(
//                        clientId, channel.getClientSkuId());
//                ChannelListingPojo obj = ChannelListingPojo.builder().channelId(savedChannel.get().getChannelId())
//                        .clientId(clientId).channelSkuId(channel.getChannelSkuId())
//                        .globalSkuId(savedProductPojo.getGlobalSkuId()).build();
//                channelDao.addSingleChannelListing(obj);
//                channelListingsListPojo.add(obj);
//            }
////            else {
////                savedChannelListingPojo.setChannelSkuId(channel.getChannelSkuId());
////                channelListingsListPojo.add(savedChannelListingPojo);
////            }
//        }
//        return channelListingsListPojo;
//    }
    
    
    public ChannelPojo getChannelByChannelName(String channelName) {
        Optional<ChannelPojo> channel = channelDao.checkChannelExistOrNot(channelName);
        if (!channel.isPresent()) {
            throw new ApiGenericException("channel doesn't exist");
        }
        return channel.get();
    }
    
    
    public ChannelPojo getChannelByNameAndType(String name, InvoiceType type) {
        ChannelPojo channelPojo = channelDao.getChannelIdByNameAndType(name, type);
        if (channelPojo == null) {
            throw new ApiGenericException("channel Not exist");
        }
        return channelPojo;
    }
    
    public ChannelListingPojo getChannelListngByClientChannelAndGlobalSkuId(Long clientId, Long channelId, Long globalSkuId,
                                                                            String channelSkuId) {
        ChannelListingPojo channelListingPojo =
                channelDao.findChannelListingBySkuIDByChannelIdAndSkuId(clientId, channelId, globalSkuId, channelSkuId);
        return channelListingPojo;
    }
    
    public Long getGlobalSkuIDByClientIdAndChannelIdAndSkuId(Long clientId, Long channelId, String channelSkuId) {
        return channelDao.getGlobalSkuIDByChannelIdAndSkuId(clientId, channelId, channelSkuId);
    }
}

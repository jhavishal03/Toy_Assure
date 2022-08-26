package com.increff.Service.impl;

import com.increff.Constants.UserType;
import com.increff.Dao.ChannelDao;
import com.increff.Dao.ProductDao;
import com.increff.Dao.UserDao;
import com.increff.Dto.ChannelDto;
import com.increff.Dto.ChannelListingCsv;
import com.increff.Exception.ApiGenericException;
import com.increff.Exception.CSVFileParsingException;
import com.increff.Model.Channel;
import com.increff.Model.ChannelListing;
import com.increff.Model.Product;
import com.increff.Model.User;
import com.increff.Service.ChannelService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChannelServiceImpl implements ChannelService {
    
    
    private ChannelDao channelDao;
    
    private UserDao userDao;
    
    private ProductDao productDao;
    
    @Autowired
    public ChannelServiceImpl(ChannelDao channelDao, UserDao userDao, ProductDao productDao) {
        
        this.channelDao = channelDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }
    
    
    @Override
    public Channel addChannel(ChannelDto channelDto) {
        Optional<Channel> isExistChannel = channelDao.checkChannelExistOrNot(channelDto.getName());
        if (isExistChannel.isPresent()) {
            throw new ApiGenericException("Channel Already Exist with channelName -> " + channelDto.getName());
        }
        Channel channel = Channel.builder().name(channelDto.getName()).
                invoiceType(channelDto.getInvoiceType()).build();
        
        return channelDao.saveChannel(channel);
    }
    
    @Override
    @Transactional
    public List<ChannelListing> addChannelListings(String clientName, String channelName, MultipartFile channelListings) {
        
        List<ChannelListingCsv> channelList = null;
        List<ChannelListing> channelListingsList = new ArrayList<>();
        Optional<User> savedUser = userDao.getUserByNameAndType(clientName, UserType.CLIENT);
        if (!savedUser.isPresent()) {
            throw new ApiGenericException("Client doesn't exist");
        }
        Optional<Channel> savedChannel = channelDao.checkChannelExistOrNot(channelName);
        if (!savedChannel.isPresent()) {
            throw new ApiGenericException("Channel doesnot exist");
        }
        try {
            channelList = new CsvToBeanBuilder(new InputStreamReader(new ByteArrayInputStream(channelListings.getBytes())))
                    .withType(ChannelListingCsv.class).withSkipLines(1).build().parse();
        } catch (Exception e) {
            throw new CSVFileParsingException(e.getCause() + e.getMessage());
        }
        Set<String> skuIds = channelList.stream().map(channl -> channl.getClientSkuId()).collect(Collectors.toSet());
        if (Integer.compare(channelList.size(), skuIds.size()) != 0) {
            throw new ApiGenericException("Duplicate Sku present in CSV file");
        }
        
        Long clientId = savedUser.get().getUserId();
        Long channelId = savedChannel.get().getChannelId();
        for (ChannelListingCsv channel : channelList) {
            
            Product product = productDao.checkProductExistByClientIdAndClientSkuId(clientId,
                    channel.getClientSkuId());
            if (product == null) {
                continue;
            }
            // if else extra lagana hai
            ChannelListing savedChannelListing =
                    channelDao.findChannelListingBySkuIDByChannelIdAndSkuId(clientId, channelId, product.getGlobalSkuId());
            if (savedChannelListing == null) {
                Product savedProduct = productDao.getProductForProductByClientIdAndClientSkuId(
                        savedUser.get().getUserId(), channel.getClientSkuId());
                ChannelListing obj = ChannelListing.builder().channelId(savedChannel.get().getChannelId())
                        .clientId(savedUser.get().getUserId()).channelSkuId(channel.getChannelSkuId())
                        .globalSkuId(savedProduct.getGlobalSkuId()).build();
                channelDao.addSingleChannelListing(obj);
                channelListingsList.add(obj);
            } else {
                savedChannelListing.setChannelSkuId(channel.getChannelSkuId());
                channelListingsList.add(savedChannelListing);
            }
        }
        return channelListingsList;
    }
}

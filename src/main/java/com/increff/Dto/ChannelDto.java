package com.increff.Dto;

import com.increff.Constants.UserType;
import com.increff.Model.ChannelForm;
import com.increff.Model.ChannelListingCsvForm;
import com.increff.Pojo.ChannelListingPojo;
import com.increff.Pojo.ChannelPojo;
import com.increff.Pojo.ProductPojo;
import com.increff.Pojo.UserPojo;
import com.increff.Service.ChannelApi;
import com.increff.Service.ProductApi;
import com.increff.Service.UserApi;
import com.increff.Util.CSVParseUtil;
import com.increff.common.Exception.ApiGenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChannelDto {
    
    private ChannelApi channelApi;
    private UserApi userApi;
    
    private ProductApi productApi;
    
    @Autowired
    public ChannelDto(ChannelApi channelApi, UserApi userApi, ProductApi productApi) {
        this.channelApi = channelApi;
        this.userApi = userApi;
        this.productApi = productApi;
    }
    
    public ChannelPojo addChannel(ChannelForm channelForm) {
        ChannelPojo channelPojo = ChannelPojo.builder().name(channelForm.getName().trim().toLowerCase()).
                invoiceType(channelForm.getInvoiceType()).build();
        return channelApi.addChannel(channelPojo);
    }
    
    public List<ChannelListingPojo> addChannelListingsDto(String clientName, String channelName, MultipartFile file) {
        List<ChannelListingCsvForm> channelList = this.parseChannelListCsv(file);
        return upsertChannelListingsData(clientName, channelName, channelList);
    }
    
    private List<ChannelListingPojo> upsertChannelListingsData(String clientName, String channelName, List<ChannelListingCsvForm> channels) {
        List<ChannelListingPojo> channelListingsListPojo = new ArrayList<>();
        
        Optional<UserPojo> savedClient = userApi.getUserByNameAndType(clientName, UserType.CLIENT);
        ChannelPojo savedChannel = channelApi.getChannelByChannelName(channelName);
        Long clientId = savedClient.get().getUserId();
        Long channelId = savedChannel.getChannelId();
        for (ChannelListingCsvForm channel : channels) {
            Long channelSkuIdPresnt = channelApi.getGlobalSkuIDByClientIdAndChannelIdAndSkuId(clientId, channelId, channel.getChannelSkuId());
            if (channelSkuIdPresnt != null) {
                throw new ApiGenericException("ChannelSkuId " + channel.getChannelSkuId() + " already in use for client " + clientId);
            }
            ProductPojo productPojo = productApi.findProductByClientIdAndSkuId(clientId,
                    channel.getClientSkuId());
            if (productPojo == null) {
                throw new ApiGenericException("ProductPojo not exist for client " + clientName + " SkuId " + channel.getClientSkuId());
            }
            // if else extra lagana hai
            ChannelListingPojo savedChannelListingPojo =
                    channelApi.getChannelListngByClientChannelAndGlobalSkuId(clientId, channelId, productPojo.getGlobalSkuId(),
                            channel.getChannelSkuId());
            if (savedChannelListingPojo == null) {
                ProductPojo savedProductPojo = productApi.findProductByClientIdAndSkuId(
                        clientId, channel.getClientSkuId());
                ChannelListingPojo obj = ChannelListingPojo.builder().channelId(savedChannel.getChannelId())
                        .clientId(clientId).channelSkuId(channel.getChannelSkuId())
                        .globalSkuId(savedProductPojo.getGlobalSkuId()).build();
                channelApi.addChannelListing(obj);
                channelListingsListPojo.add(obj);
            }
            
        }
        return channelListingsListPojo;
    }
    
    private List<ChannelListingCsvForm> parseChannelListCsv(MultipartFile file) {
        List<ChannelListingCsvForm> channelList = null;
        try {
            channelList = CSVParseUtil.parseCSV(file.getBytes(), ChannelListingCsvForm.class);
        } catch (IOException e) {
            throw new ApiGenericException("CSV IO exception while reading");
        }
        Set<String> skuIds = new HashSet<>();
        Set<String> duplicateIds = channelList.stream().map(channel -> channel.getChannelSkuId())
                .filter(channel -> !skuIds.add(channel)).collect(Collectors.toSet());
        if (duplicateIds.size() != 0) {
            throw new ApiGenericException("Duplicate channelSkuId present in CSV file with sku-> " + duplicateIds);
            
        }
        return channelList;
    }
    
}

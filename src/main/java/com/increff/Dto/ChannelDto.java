package com.increff.Dto;

import com.increff.Exception.ApiGenericException;
import com.increff.Exception.CSVFileParsingException;
import com.increff.Model.ChannelForm;
import com.increff.Model.ChannelListingCsv;
import com.increff.Pojo.Channel;
import com.increff.Pojo.ChannelListing;
import com.increff.Service.ChannelService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChannelDto {
    @Autowired
    private ChannelService channelService;
    
    public Channel addChannel(ChannelForm channelForm) {
        Channel channel = Channel.builder().name(channelForm.getName()).
                invoiceType(channelForm.getInvoiceType()).build();
        return channelService.addChannel(channel);
    }
    
    public List<ChannelListing> addChannelListingsDto(String clientName, String channelName, MultipartFile file) {
        List<ChannelListingCsv> channelList = null;
        try {
            channelList = new CsvToBeanBuilder(new InputStreamReader(new ByteArrayInputStream(file.getBytes())))
                    .withType(ChannelListingCsv.class).withSkipLines(1).build().parse();
        } catch (Exception e) {
            throw new CSVFileParsingException(e.getMessage());
        }
        Set<String> skuIds = new HashSet<>();
        Set<String> duplicateIds = channelList.stream().map(channel -> channel.getClientSkuId())
                .filter(channel -> !skuIds.add(channel)).collect(Collectors.toSet());
        if (duplicateIds.size() != 0) {
            throw new ApiGenericException("Duplicate Sku present in CSV file with sku-> " + duplicateIds);
            
        }
        return channelService.addChannelListings(clientName, channelName, channelList);
    }
    
}

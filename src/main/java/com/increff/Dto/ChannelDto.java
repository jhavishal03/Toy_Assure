package com.increff.Dto;

import com.increff.Exception.ApiGenericException;
import com.increff.Model.ChannelForm;
import com.increff.Model.ChannelListingCsvForm;
import com.increff.Pojo.Channel;
import com.increff.Pojo.ChannelListing;
import com.increff.Service.ChannelService;
import com.increff.Util.CSVParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChannelDto {
    @Autowired
    private ChannelService channelService;
    
    public Channel addChannel(ChannelForm channelForm) {
        Channel channel = Channel.builder().name(channelForm.getName().trim().toLowerCase()).
                invoiceType(channelForm.getInvoiceType()).build();
        return channelService.addChannel(channel);
    }
    
    public List<ChannelListing> addChannelListingsDto(String clientName, String channelName, MultipartFile file) {
        List<ChannelListingCsvForm> channelList = null;
        try {
            channelList = CSVParseUtil.parseCSV(file.getBytes(), ChannelListingCsvForm.class);
//            channelList = new CsvToBeanBuilder(new InputStreamReader(new ByteArrayInputStream(file.getBytes())))
//                    .withType(ChannelListingCsv.class).withSkipLines(1).build().parse();
        } catch (IOException e) {
            throw new ApiGenericException("CSV IO exception while reading");
        }
        Set<String> skuIds = new HashSet<>();
        Set<String> duplicateIds = channelList.stream().map(channel -> channel.getChannelSkuId())
                .filter(channel -> !skuIds.add(channel)).collect(Collectors.toSet());
        if (duplicateIds.size() != 0) {
            throw new ApiGenericException("Duplicate channelSkuId present in CSV file with sku-> " + duplicateIds);
            
        }
        return channelService.addChannelListings(clientName, channelName, channelList);
    }
    
}

package com.increff.Dto.Converter;

import com.increff.Dto.ChannelDto;
import com.increff.Model.Channel;
import org.springframework.stereotype.Component;

@Component
public class ChannelConverter {

    public Channel convertChannelDtoToChannel(ChannelDto channelDto) {
        Channel channel = new Channel();
        channel.setName(channelDto.getName());
        channel.setInvoiceType(channelDto.getInvoiceType());
        return channel;
    }
}

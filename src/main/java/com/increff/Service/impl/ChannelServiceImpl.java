package com.increff.Service.impl;

import com.increff.Dao.ChannelDao;
import com.increff.Dto.ChannelDto;
import com.increff.Dto.Converter.ChannelConverter;
import com.increff.Exception.UserException;
import com.increff.Model.Channel;
import com.increff.Service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelServiceImpl implements ChannelService {
    private ChannelConverter channelConverter;

    private ChannelDao channelDao;

    @Autowired
    public ChannelServiceImpl(ChannelConverter channelConverter, ChannelDao channelDao) {
        this.channelConverter = channelConverter;
        this.channelDao = channelDao;
    }

    @Override
    public Channel addChannel(ChannelDto channelDto) {
        Long isExistChannel = channelDao.checkChannelExistOrNot(channelDto.getName());
        if (isExistChannel == 1) {
            throw new UserException("Channel Already Exist with channelName -> " + channelDto.getName());
        }
        Channel channel = channelConverter.convertChannelDtoToChannel(channelDto);

        return channelDao.saveChannel(channel);
    }
}

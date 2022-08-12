package com.increff.Dto;

import com.opencsv.bean.CsvBindByPosition;

public class ChannelListingCsv {
    @CsvBindByPosition(position = 0)
    private String channelSkuId;
    @CsvBindByPosition(position = 1)
    private String clientSkuId;

    public ChannelListingCsv() {
    }

    public String getChannelSkuId() {
        return channelSkuId;
    }

    public void setChannelSkuId(String channelSkuId) {
        this.channelSkuId = channelSkuId;
    }

    public String getClientSkuId() {
        return clientSkuId;
    }

    public void setClientSkuId(String clientSkuId) {
        this.clientSkuId = clientSkuId;
    }
}

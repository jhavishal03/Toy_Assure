package com.increff.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ChannelListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channelListingId;
    private Long channelId;
    private Long channelSkuId;
    private Long clientId;
    private Long globalSkuId;

    public ChannelListing() {
    }

    public Long getChannelListingId() {
        return channelListingId;
    }

    public void setChannelListingId(Long channelListingId) {
        this.channelListingId = channelListingId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getChannelSkuId() {
        return channelSkuId;
    }

    public void setChannelSkuId(Long channelSkuId) {
        this.channelSkuId = channelSkuId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getGlobalSkuId() {
        return globalSkuId;
    }

    public void setGlobalSkuId(Long globalSkuId) {
        this.globalSkuId = globalSkuId;
    }
}

package com.increff.Dao;

import com.increff.Constants.InvoiceType;
import com.increff.Pojo.Channel;
import com.increff.Pojo.ChannelListing;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ChannelDao extends AbstractDao {
    private static String checkChannelNameExistOrNot = "select c from Channel c where name=:channelName";
    private static String getChannelIdByNameAndType = "select c from Channel c where name=:name and invoiceType=:type ";
    
    
    private static String getGlobalIdByClientIdAndChannelSkuID =
            "select globalSkuId from ChannelListing c where channelId=:channelId and " + " " +
                    " clientId=:clientId and channelSkuId=:skuId";
    
    private static String findChannelListingByChannelIdAndGlobalSkuId = "select c from ChannelListing c where channelId=:channelId and " + " " +
            " clientId=:clientId and globalSkuId=:globalSkuId and channelSkuId=:skuId";
    
    public Optional<Channel> checkChannelExistOrNot(String channelName) {
        TypedQuery<Channel> query = getQuery(checkChannelNameExistOrNot, Channel.class);
        query.setParameter("channelName", channelName);
        return query.getResultList().stream().findFirst();
    }
    
    @Transactional
    public Channel saveChannel(Channel channel) {
        em.persist(channel);
        return channel;
    }
    
    public ChannelListing findChannelListingBySkuIDByChannelIdAndSkuId(Long clientId, Long channelId, Long globalSkuId
            , String skuId) {
        TypedQuery<ChannelListing> query = getQuery(findChannelListingByChannelIdAndGlobalSkuId, ChannelListing.class);
        query.setParameter("clientId", clientId);
        query.setParameter("channelId", channelId);
        query.setParameter("globalSkuId", globalSkuId);
        query.setParameter("skuId", skuId);
        return getSingle(query);
    }
    
    public Channel getChannelIdByNameAndType(String name, InvoiceType type) {
        TypedQuery<Channel> query = getQuery(getChannelIdByNameAndType, Channel.class);
        query.setParameter("name", name);
        query.setParameter("type", type);
        return getSingle(query);
    }
    
    public Long getGlobalSkuIDByChannelIdAndSkuId(Long clientId, Long channelId, String skuId) {
        TypedQuery<Long> query = getQuery(getGlobalIdByClientIdAndChannelSkuID, Long.class);
        query.setParameter("clientId", clientId);
        query.setParameter("channelId", channelId);
        query.setParameter("skuId", skuId);
        return getSingle(query);
    }
    
    @Transactional
    public List<ChannelListing> saveChannelsListing(List<ChannelListing> channelListings) {
        List<ChannelListing> result = new ArrayList<>();
        for (ChannelListing ch : channelListings) {
            em.persist(ch);
            result.add(ch);
        }
        return result;
    }
    
    public ChannelListing addSingleChannelListing(ChannelListing obj) {
        em.persist(obj);
        return obj;
    }
}

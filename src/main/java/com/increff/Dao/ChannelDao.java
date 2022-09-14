package com.increff.Dao;

import com.increff.Constants.InvoiceType;
import com.increff.Pojo.ChannelListingPojo;
import com.increff.Pojo.ChannelPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ChannelDao extends AbstractDao {
    private static String checkChannelNameExistOrNot = "select c from ChannelPojo c where name=:channelName";
    private static String getChannelIdByNameAndType = "select c from ChannelPojo c where name=:name and invoiceType=:type ";
    
    
    private static String getGlobalIdByClientIdAndChannelSkuID =
            "select globalSkuId from ChannelListingPojo c where channelId=:channelId and " + " " +
                    " clientId=:clientId and channelSkuId=:skuId";
    
    private static String findChannelListingByChannelIdAndGlobalSkuId = "select c from ChannelListingPojo c where " +
            "channelId=:channelId and clientId=:clientId and globalSkuId=:globalSkuId and channelSkuId=:skuId";
    
    public Optional<ChannelPojo> checkChannelExistOrNot(String channelName) {
        TypedQuery<ChannelPojo> query = getQuery(checkChannelNameExistOrNot, ChannelPojo.class);
        query.setParameter("channelName", channelName);
        return query.getResultList().stream().findFirst();
    }
    
    public ChannelPojo saveChannel(ChannelPojo channelPojo) {
        em.persist(channelPojo);
        return channelPojo;
    }
    
    public ChannelListingPojo findChannelListingBySkuIDByChannelIdAndSkuId(Long clientId, Long channelId, Long globalSkuId
            , String skuId) {
        TypedQuery<ChannelListingPojo> query = getQuery(findChannelListingByChannelIdAndGlobalSkuId, ChannelListingPojo.class);
        query.setParameter("clientId", clientId);
        query.setParameter("channelId", channelId);
        query.setParameter("globalSkuId", globalSkuId);
        query.setParameter("skuId", skuId);
        return getSingle(query);
    }
    
    public ChannelPojo getChannelIdByNameAndType(String name, InvoiceType type) {
        TypedQuery<ChannelPojo> query = getQuery(getChannelIdByNameAndType, ChannelPojo.class);
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
    public List<ChannelListingPojo> saveChannelsListing(List<ChannelListingPojo> channelListingPojos) {
        List<ChannelListingPojo> result = new ArrayList<>();
        for (ChannelListingPojo ch : channelListingPojos) {
            em.persist(ch);
            result.add(ch);
        }
        return result;
    }
    
    public ChannelListingPojo addSingleChannelListing(ChannelListingPojo obj) {
        em.persist(obj);
        return obj;
    }
}

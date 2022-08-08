package com.increff.Dao;

import com.increff.Constants.InvoiceType;
import com.increff.Model.Channel;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Repository
public class ChannelDao extends AbstractDao {
    private static String checkChannelNameExistOrNot = "select count(c) from Channel c where name=:channelName";
    private static String getChannelIdByNameAndType = "select channelId from Channel c where name=:name and invoiceType=:type ";


    public Long checkChannelExistOrNot(String channelName) {
        TypedQuery<Long> query = getQuery(checkChannelNameExistOrNot, Long.class);
        query.setParameter("channelName", channelName);
        return query.getSingleResult();
    }

    @Transactional
    public Channel saveChannel(Channel channel) {
        em.persist(channel);
        return channel;
    }

    public Long getChannelIdByNameAndType(String name, InvoiceType type) {
        TypedQuery<Long> query = getQuery(getChannelIdByNameAndType, Long.class);
        query.setParameter("name", name);
        query.setParameter("type", type);
        return getSingle(query);
    }
}

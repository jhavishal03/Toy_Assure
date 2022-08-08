package com.increff.Dao;

import com.increff.Model.Bin;
import com.increff.Model.BinSku;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class BinDao extends AbstractDao {

    private static String getAlBinIds = "select b.binId from Bin b";
    private static String getBinEntityDataBySkuIDAndBinID = "select bs from BinSku bs where globalSkuId=:skuId and binId=:binId ";


    @Transactional
    public void addBinToSystem(int n) {
        for (int i = 0; i < n; i++) {
            em.persist(new Bin());
        }
    }

    public BinSku getBinEntityByBinIdAndSkuId(Long skuId, Long binId) {
        TypedQuery<BinSku> query = getQuery(getBinEntityDataBySkuIDAndBinID, BinSku.class);
        query.setParameter("skuId", skuId);
        query.setParameter("binId", binId);
        return getSingle(query);
    }

    public List<Long> getAllBinIds() {

        TypedQuery<Long> query = getQuery(getAlBinIds, Long.class);
        return query.getResultList();
    }

    @Transactional
    public List<BinSku> uploadBinDataInventory(Set<BinSku> binSkus) {
        List<BinSku> result = new ArrayList<>();
        for (BinSku bin : binSkus) {
            em.persist(bin);
            result.add(bin);
        }
        return result;
    }

    @Transactional
    public List<BinSku> updateBinDataInventory(Set<BinSku> binSkus) {
        List<BinSku> result = new ArrayList<>();
        for (BinSku bin : binSkus) {
            em.merge(bin);
            result.add(bin);
        }
        return result;
    }

}

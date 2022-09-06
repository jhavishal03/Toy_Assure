package com.increff.Dao;

import com.increff.Pojo.Bin;
import com.increff.Pojo.BinSku;
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
    
    private static String findAllTheBinContainingProductByGlobalSku =
            "select bs from BinSku bs  where globalSkuId=:skuId order by quantity desc";
    
    @Transactional
    public List<Long> addBinToSystem(int n) {
        List<Long> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Bin bin = new Bin();
            em.persist(bin);
            res.add(bin.getBinId());
        }
        return res;
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
    
    public List<BinSku> getAllBinsContainingProductBySku(Long skuId) {
        TypedQuery<BinSku> query = getQuery(findAllTheBinContainingProductByGlobalSku, BinSku.class);
        query.setParameter("skuId", skuId);
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
    
    
}

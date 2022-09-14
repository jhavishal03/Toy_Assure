package com.increff.Dao;

import com.increff.Pojo.BinPojo;
import com.increff.Pojo.BinSkuPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class BinDao extends AbstractDao {
    
    private static String getAlBinIds = "select b.binId from BinPojo b";
    private static String getBinEntityDataBySkuIDAndBinID = "select bs from BinSkuPojo bs where " +
            "globalSkuId=:skuId and binId=:binId ";
    
    private static String findAllTheBinContainingProductByGlobalSku =
            "select bs from BinSkuPojo bs  where globalSkuId=:skuId order by quantity desc";
    
    @Transactional
    public List<Long> addBinToSystem(Long n) {
        List<Long> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            BinPojo binPojo = new BinPojo();
            em.persist(binPojo);
            res.add(binPojo.getBinId());
        }
        return res;
    }
    
    public BinSkuPojo getBinEntityByBinIdAndSkuId(Long skuId, Long binId) {
        TypedQuery<BinSkuPojo> query = getQuery(getBinEntityDataBySkuIDAndBinID, BinSkuPojo.class);
        query.setParameter("skuId", skuId);
        query.setParameter("binId", binId);
        return getSingle(query);
    }
    
    public List<Long> getAllBinIds() {
        
        TypedQuery<Long> query = getQuery(getAlBinIds, Long.class);
        return query.getResultList();
    }
    
    public List<BinSkuPojo> getAllBinsContainingProductBySku(Long skuId) {
        TypedQuery<BinSkuPojo> query = getQuery(findAllTheBinContainingProductByGlobalSku, BinSkuPojo.class);
        query.setParameter("skuId", skuId);
        return query.getResultList();
    }
    
    @Transactional
    public List<BinSkuPojo> uploadBinDataInventory(Set<BinSkuPojo> binSkusPojos) {
        List<BinSkuPojo> result = new ArrayList<>();
        for (BinSkuPojo bin : binSkusPojos) {
            em.persist(bin);
            result.add(bin);
        }
        return result;
    }
    
    
}

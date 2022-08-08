package com.increff.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BinSku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long binSkuId;
    private Long binId;
    private Long globalSkuId;
    private Long quantity;

    public BinSku() {

    }

    public BinSku(Long binId, Long globalSkuId, Long quantity) {
        this.binId = binId;
        this.globalSkuId = globalSkuId;
        this.quantity = quantity;
    }

    public Long getBinSkuId() {
        return binSkuId;
    }

    public void setBinSkuId(Long binSkuId) {
        this.binSkuId = binSkuId;
    }

    public Long getBinId() {
        return binId;
    }

    public void setBinId(Long binId) {
        this.binId = binId;
    }

    public Long getGlobalSkuId() {
        return globalSkuId;
    }

    public void setGlobalSkuId(Long globalSkuId) {
        this.globalSkuId = globalSkuId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}

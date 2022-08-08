package com.increff.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;
    private Long globalSkuId;
    private Long availableQuantity;
    private Long allocatedQuantity;
    private Long fulfilledQuantity;

    public Inventory() {
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Long getGlobalSkuId() {
        return globalSkuId;
    }

    public void setGlobalSkuId(Long globalSkuId) {
        this.globalSkuId = globalSkuId;
    }

    public Long getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Long availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Long getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public void setAllocatedQuantity(Long allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
    }

    public Long getFulfilledQuantity() {
        return fulfilledQuantity;
    }

    public void setFulfilledQuantity(Long fulfilledQuantity) {
        this.fulfilledQuantity = fulfilledQuantity;
    }
}

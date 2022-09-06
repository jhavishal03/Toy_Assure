package com.increff.Dto;

import com.opencsv.bean.CsvBindByPosition;

public class OrderItemCsvDto {
    @CsvBindByPosition(position = 0, required = true)
    private String clientSkuId;
    @CsvBindByPosition(position = 1, required = true)
    private Long orderedQuantity;
    @CsvBindByPosition(position = 2, required = true)
    private Double sellingPricePerUnit;
    
    public OrderItemCsvDto() {
    }
    
    public String getClientSkuId() {
        return clientSkuId;
    }
    
    public void setClientSkuId(String clientSkuId) {
        this.clientSkuId = clientSkuId;
    }
    
    public Long getOrderedQuantity() {
        return orderedQuantity;
    }
    
    public void setOrderedQuantity(Long orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }
    
    public Double getSellingPricePerUnit() {
        return sellingPricePerUnit;
    }
    
    public void setSellingPricePerUnit(Double sellingPricePerUnit) {
        this.sellingPricePerUnit = sellingPricePerUnit;
    }
}

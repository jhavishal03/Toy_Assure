package com.increff.Model;

import com.increff.Util.MustAValidNumber;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.validators.PreAssignmentValidator;

public class OrderItemCsvForm {
    @CsvBindByPosition(position = 0, required = true)
    private String clientSkuId;
    @CsvBindByPosition(position = 1, required = true)
    @PreAssignmentValidator(validator = MustAValidNumber.class)
    private Long orderedQuantity;
    @CsvBindByPosition(position = 2, required = true)
    @PreAssignmentValidator(validator = MustAValidNumber.class)
    private Double sellingPricePerUnit;
    
    public OrderItemCsvForm() {
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

package com.increff.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@Getter
@Setter
@Builder
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceItemData {
    
    @XmlElement(name = "client-sku-id")
    private String clientSkuid;
    
    @XmlElement(name = "product-name")
    private String productName;
    
    @XmlElement(name = "quantity")
    private Long quantity;
    
    @XmlElement(name = "selling-price-per-unit")
    private Double sellingPricePerUnit;
    
    @XmlElement(name = "amount")
    private Double amount;
    
    public InvoiceItemData() {
    }
}
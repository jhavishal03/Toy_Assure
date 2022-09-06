package com.increff.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "invoice-record")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class InvoiceData {
    
    @XmlElement(name = "client-name")
    private String clientName;
    
    @XmlElement(name = "invoice-number")
    private Long invoiceNumber;
    
    @XmlElement(name = "invoice-date")
    private String invoiceDate;
    
    @XmlElement(name = "invoice-total")
    private Double invoiceTotal;
    
    @XmlElement(name = "line-item-record")
    @XmlElementWrapper(name = "line-item-records")
    private List<InvoiceItemData> invoiceItemData;
    
    public InvoiceData() {
    }
}

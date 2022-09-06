package com.increff.Model;

import com.increff.Constants.InvoiceType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ChannelDto {
    @NotEmpty
    private String name;
    @NotNull
    private InvoiceType invoiceType;
    
    public ChannelDto() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public InvoiceType getInvoiceType() {
        return invoiceType;
    }
    
    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }
}
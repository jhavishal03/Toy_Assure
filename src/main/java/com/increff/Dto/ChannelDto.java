package com.increff.Dto;

import com.increff.Constants.InvoiceType;

public class ChannelDto {
    private String name;
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

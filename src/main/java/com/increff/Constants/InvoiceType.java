package com.increff.Constants;

public enum InvoiceType {
    CHANNEL("Channel"),SELF("Self");
    private String value;
    InvoiceType(String value) {
        this.value=value;
    }
    public String getValue(){
        return this.value;
    }
}

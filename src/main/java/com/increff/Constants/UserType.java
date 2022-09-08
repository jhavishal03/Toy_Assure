package com.increff.Constants;

public enum UserType {
    CLIENT("Client"), CUSTOMER("Customer");
    private String value;
    
    UserType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}

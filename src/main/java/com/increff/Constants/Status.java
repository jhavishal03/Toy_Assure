package com.increff.Constants;

public enum Status {
    CREATED("Created"), ALLOCATED("Allocated"), FULFILLED("Fulfilled");
    private String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}

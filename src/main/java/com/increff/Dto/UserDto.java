package com.increff.Dto;

import com.increff.Constants.UserType;

import javax.validation.constraints.NotEmpty;

public class UserDto {
    @NotEmpty
    private String name;
    private UserType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }
}

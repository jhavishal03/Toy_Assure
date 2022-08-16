package com.increff.Dto;

import com.increff.Constants.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotEmpty
    private String name;
    private UserType type;

}

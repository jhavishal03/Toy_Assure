package com.increff.Model;

import com.increff.Constants.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {
    @NotEmpty
    private String name;
    @NotNull
    private UserType type;
    
}

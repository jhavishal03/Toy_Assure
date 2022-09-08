package com.increff.Model;

import com.increff.Constants.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {
    @NotEmpty
    private String name;
    
    private UserType type;
    
}

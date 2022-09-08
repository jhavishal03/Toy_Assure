package com.increff.Model;

import com.increff.Constants.UserType;
import com.increff.Util.ValueOfEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Test1 {
    
    @ValueOfEnum(enumClass = UserType.class)
    private String value;
}

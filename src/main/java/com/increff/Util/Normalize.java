package com.increff.Util;

import com.increff.Model.UserForm;

import java.util.ArrayList;
import java.util.List;

public class Normalize {
    public static void normalize(UserForm userForm) {
        userForm.setName(StringUtil.toLowerCase(userForm.getName()));
    }
    
    public static List<String> normalize(String... value) {
        List<String> res = new ArrayList<>();
        for (String data : value) {
            StringUtil.toLowerCase(data);
            res.add(data);
        }
        return res;
    }
    
}

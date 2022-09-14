package com.increff.Dto;

import com.increff.Model.UserForm;
import com.increff.Pojo.UserPojo;
import com.increff.Service.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.increff.Model.Helper.DtoHelper.check;
import static com.increff.Util.Normalize.normalize;

@Service
public class UserDto {
    
    @Autowired
    UserApi userApi;
    
    public UserPojo createUserDto(UserForm userForm) {
        check(userForm);
        normalize(userForm);
        UserPojo userPojo = UserPojo.builder().name(userForm.getName()).
                type(userForm.getType()).build();
        return userApi.addUser(userPojo);
    }
    
    public UserPojo findUserByIdDto(Long id) {
        check(id, "userId ");
        return userApi.findUserById(id);
    }
    
    
}

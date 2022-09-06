package com.increff.Dto;

import com.increff.Model.UserForm;
import com.increff.Pojo.User;
import com.increff.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDto {
    
    @Autowired
    UserService userService;
    
    public User createUserDto(UserForm userForm) {
        User user = User.builder().name(userForm.getName()).
                type(userForm.getType()).build();
        return userService.addUser(user);
    }
    
    
    public User findUserByIdDto(Long id) {
        return userService.findUserById(id);
    }
}

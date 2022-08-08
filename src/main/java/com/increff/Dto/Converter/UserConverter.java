package com.increff.Dto.Converter;

import com.increff.Dto.UserDto;
import com.increff.Model.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public User userDtoToUserConverter(UserDto userDto){
        User user=new User();
        user.setName(userDto.getName());
        user.setType(userDto.getType());
        return user;
    }
}

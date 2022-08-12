package com.increff.Service;

import com.increff.Dto.UserDto;
import com.increff.Model.User;

public interface UserService {

    public User addUser(UserDto user);

    public User findUserById(Long id);

}

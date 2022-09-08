package com.increff.Service;

import com.increff.Constants.UserType;
import com.increff.Pojo.User;

import java.util.Optional;

public interface UserService {
    
    public User addUser(User user);
    
    public User findUserById(Long id);
    
    public Optional<User> getUserByNameAndType(String name, UserType type);
    
}

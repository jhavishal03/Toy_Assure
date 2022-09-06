package com.increff.Service;

import com.increff.Pojo.User;

public interface UserService {
    
    public User addUser(User user);
    
    public User findUserById(Long id);
    
}

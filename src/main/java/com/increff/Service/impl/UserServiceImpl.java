package com.increff.Service.impl;

import com.increff.Dao.UserDao;
import com.increff.Exception.ApiGenericException;
import com.increff.Pojo.User;
import com.increff.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    
    @Override
    public User addUser(User user) {
        Optional<User> savedUser = userDao.getUserByNameAndType(user.getName(), user.getType());
        if (savedUser.isPresent() || savedUser.get().getName().equalsIgnoreCase(user.getName())) {
            throw new ApiGenericException("User Already Present with name " + user.getName());
        }
        
        User usr = User.builder().name(user.getName()).type(user.getType()).build();
        return userDao.addUser(usr);
    }
    
    @Override
    public User findUserById(Long id) {
        Optional<User> user = Optional.ofNullable(userDao.findUserById(id));
        if (!user.isPresent()) {
            throw new ApiGenericException("User not present with id " + id);
        }
        return user.get();
    }
}

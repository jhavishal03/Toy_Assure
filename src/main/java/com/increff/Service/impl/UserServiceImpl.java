package com.increff.Service.impl;

import com.increff.Dao.UserDao;
import com.increff.Dto.UserDto;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.User;
import com.increff.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    
    @Override
    public User addUser(UserDto userDto) {
        Optional<User> savedUser = userDao.getUserByNameAndType(userDto.getName(), userDto.getType());
        if (savedUser.isPresent()) {
            throw new ApiGenericException("User Already Present with name " + userDto.getName());
        }
        
        User user = User.builder().name(userDto.getName()).type(userDto.getType()).build();
        return userDao.addUser(user);
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

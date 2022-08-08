package com.increff.Service.impl;

import com.increff.Dao.UserDao;
import com.increff.Dto.Converter.UserConverter;
import com.increff.Dto.UserDto;
import com.increff.Exception.UserException;
import com.increff.Model.User;
import com.increff.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private UserConverter converter;
    @Autowired
    public UserServiceImpl(UserDao userDao,UserConverter converter) {
        this.userDao = userDao;
        this.converter=converter;
    }

    @Override
    public User addUser(UserDto userDto) {
        User user=converter.userDtoToUserConverter(userDto);
        return userDao.addUser(user);
    }

    @Override
    public User findUserById(Long id) {
        Optional<User> user= Optional.ofNullable(userDao.findUserById(id));
        if(!user.isPresent()){
            throw new UserException("User not present with id "+ id);
        }
        return user.get();
    }
}

package com.increff.Service;

import com.increff.Constants.Constants;
import com.increff.Constants.UserType;
import com.increff.Dao.UserDao;
import com.increff.Exception.ApiGenericException;
import com.increff.Pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserApi {
    @Autowired
    private UserDao userDao;
    
    
    public UserPojo addUser(UserPojo userPojo) {
        Optional<UserPojo> user = userDao.getUserByNameAndType(userPojo.getName(), userPojo.getType());
        if (user.isPresent()) {
            throw new ApiGenericException("User already exist by name " + userPojo.getName() + " and usertype" + userPojo.getType());
        }
        return userDao.addUser(userPojo);
    }
    
    
    public UserPojo findUserById(Long id) {
        Optional<UserPojo> user = Optional.ofNullable(userDao.findUserById(id));
        if (!user.isPresent()) {
            throw new ApiGenericException("User not present with id " + id);
        }
        return user.get();
    }
    
    public UserPojo findUserById(Long id, UserType type) {
        Optional<UserPojo> user = Optional.ofNullable(userDao.findUserById(id));
        if (!user.isPresent()) {
            throw new ApiGenericException(type.getValue().toString() + "not present with id " + id);
        }
        if (!user.get().getType().equals(type)) {
            throw new ApiGenericException("Given User is not " + type.getValue().toString());
        }
        return user.get();
    }
    
    public Optional<UserPojo> getUserByNameAndType(String name, UserType type) {
        Optional<UserPojo> savedUser = userDao.getUserByNameAndType(name.toLowerCase(), type);
        if (!savedUser.isPresent()) {
            throw new ApiGenericException(type.getValue().toString() + Constants.DOES_NOT_EXIST + " with name " + name);
        }
        return savedUser;
    }
    
    
}

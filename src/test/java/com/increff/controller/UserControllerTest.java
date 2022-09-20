package com.increff.controller;

import com.increff.Config.AbstractUnitTest;
import com.increff.Constants.UserType;
import com.increff.Model.UserForm;
import com.increff.Pojo.UserPojo;
import com.increff.Service.UserApi;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class UserControllerTest extends AbstractUnitTest {
    @Autowired
    private UserController userController;
    @Autowired
    private UserApi userApi;
    
    @Test
    public void createUserTest() {
        UserForm userForm = UserForm.builder().name("Puma").type(UserType.CLIENT).build();
        userController.addUser(userForm);
        Optional<UserPojo> userPojo = userApi.getUserByNameAndType("puma", UserType.CLIENT);
        Assert.assertNotNull(userPojo.get());
    }
    
    public void getUserByIdTest() {
        userApi.findUserById(1l);
    }
}

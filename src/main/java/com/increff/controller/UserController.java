package com.increff.controller;

import com.increff.Dto.UserDto;
import com.increff.Model.User;
import com.increff.Service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api
@RequestMapping("/api")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    @ApiOperation(value = "Api to fetch customer details by Id")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.findUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @ApiOperation(value = "Api to add Customer")
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<User> addUser(@RequestBody @Valid UserDto user) {
        return new ResponseEntity<>(userService.addUser(user), HttpStatus.CREATED);
    }

}

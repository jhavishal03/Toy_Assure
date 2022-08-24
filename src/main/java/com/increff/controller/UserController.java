package com.increff.controller;

import com.increff.Dto.Response;
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
    public ResponseEntity<Response> getUserById(@PathVariable("id") Long id) {
        User user = userService.findUserById(id);
        Response response = new Response<>("User details for Id -> " + id, user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Api to add Customer")
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<Response> addUser(@RequestBody @Valid UserDto user) {
        User result = userService.addUser(user);
        Response response = new Response<>("User data created", user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
}

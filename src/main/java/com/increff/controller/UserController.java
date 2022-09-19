package com.increff.controller;

import com.increff.Dto.UserDto;
import com.increff.Model.Response;
import com.increff.Model.Test1;
import com.increff.Model.UserForm;
import com.increff.Pojo.UserPojo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api
@RequestMapping("/api")
@Validated
public class UserController {
    @Autowired
    private UserDto userDto;
    
    
    @GetMapping("/get-user/{id}")
    @ApiOperation(value = "Api to fetch customer details by Id")
    public ResponseEntity<Response> getUserById(@PathVariable("id") Long id) {
        UserPojo userPojo = userDto.findUserByIdDto(id);
        Response response = new Response<>("UserPojo details for Id -> " + id, userPojo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Api to add Customer")
    @RequestMapping(value = "/create-user", method = RequestMethod.POST)
    public ResponseEntity<Response> addUser(@RequestBody UserForm user) {
        UserPojo result = userDto.createUserDto(user);
        Response response = new Response<>("UserPojo data created", user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Api to add Customer")
    @RequestMapping(value = "/create-test", method = RequestMethod.POST)
    public ResponseEntity<Response> addUserTest(@RequestBody @Valid Test1 user) {
        
        Response response = new Response<>("UserPojo data created", user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

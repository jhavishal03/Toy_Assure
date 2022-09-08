package com.increff.controller;

import com.increff.Dto.UserDto;
import com.increff.Model.Response;
import com.increff.Model.Test1;
import com.increff.Model.UserForm;
import com.increff.Pojo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@Api
@RequestMapping("/api")
@Validated
public class UserController {
    @Autowired
    private UserDto userDto;
    
    
    @GetMapping("/get-user/{id}")
    @ApiOperation(value = "Api to fetch customer details by Id")
    public ResponseEntity<Response> getUserById(@PathVariable("id") @Min(0) Long id) {
        User user = userDto.findUserByIdDto(id);
        Response response = new Response<>("User details for Id -> " + id, user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Api to add Customer")
    @RequestMapping(value = "/create-user", method = RequestMethod.POST)
    public ResponseEntity<Response> addUser(@RequestBody @Valid UserForm user) {
        User result = userDto.createUserDto(user);
        Response response = new Response<>("User data created", user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Api to add Customer")
    @RequestMapping(value = "/create-test", method = RequestMethod.POST)
    public ResponseEntity<Response> addUserTest(@RequestBody @Valid Test1 user) {
        
        Response response = new Response<>("User data created", user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

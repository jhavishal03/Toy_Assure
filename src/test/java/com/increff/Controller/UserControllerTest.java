package com.increff.Controller;

import com.increff.Constants.UserType;
import com.increff.Dto.UserDto;
import com.increff.Model.User;
import com.increff.Service.UserService;
import com.increff.Util.TestUtil;
import com.increff.controller.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    
    @Test
    public void getUserByUserIdTest_Success() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        
        when(userService.findUserById(anyLong())).thenReturn(TestUtil.getUserClient());
        ResponseEntity<User> responseEntity = userController.getUserById(1L);
        
        assertEquals(responseEntity.getStatusCodeValue(), 200);
    }
    
    //    @Test
    public void getUserByUserIdTest_Success1() throws Exception {
        when(userService.findUserById(anyLong())).thenReturn(TestUtil.getUserClient());
        mockMvc.perform(get("/user/{id}", 1l)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        
    }
    
    @Test
    public void addUserTest_Success() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        UserDto user = new UserDto("mockUser", UserType.CLIENT);
        when(userService.addUser(any())).thenReturn(TestUtil.getUserClient());
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/user").content())
        ResponseEntity<User> responseEntity = userController.addUser(user);
        assertEquals(responseEntity.getStatusCodeValue(), 201);
        assertEquals(responseEntity.getBody().getType(), UserType.CLIENT);
        assertEquals(responseEntity.getBody().getName(), "MockUser");
    }
    
    
}

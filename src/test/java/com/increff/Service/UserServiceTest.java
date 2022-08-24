package com.increff.Service;

import com.increff.Dao.UserDao;
import com.increff.Dto.UserDto;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.User;
import com.increff.Service.impl.UserServiceImpl;
import com.increff.Util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    
    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    public void addUserTest_Success() {
        when(userDao.getUserByNameAndType((String) notNull(), any())).thenReturn(Optional.empty());
        when(userDao.addUser(any())).thenReturn(TestUtil.getUserClient());
        UserDto userDto = TestUtil.getUserDto();
        assertNotNull(userService.addUser(userDto));
    }
    
    @Test(expected = ApiGenericException.class)
    public void addUserTest_Fail() {
        when(userDao.getUserByNameAndType((String) notNull(), any())).thenReturn(Optional.of(TestUtil.getUserClient()));
        UserDto userDto = TestUtil.getUserDto();
        userService.addUser(userDto);
    }
    
    @Test
    public void findUserByIdTest_Success() {
        when(userDao.findUserById((Long) notNull())).thenReturn(TestUtil.getUserClient());
        User user = userService.findUserById(anyLong());
        assertNotNull(user);
        String name = "MockUser";
//        assertEquals(user.getType(), UserType.CLIENT);
    }
    
    @Test(expected = ApiGenericException.class)
    public void findUserByIdTest_Fail() {
        when(userDao.findUserById((Long) notNull())).thenReturn(null);
        User user = userService.findUserById((Long) notNull());
        
    }
}

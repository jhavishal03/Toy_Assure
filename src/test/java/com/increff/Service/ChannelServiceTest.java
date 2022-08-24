package com.increff.Service;

import com.increff.Dao.ChannelDao;
import com.increff.Dao.ProductDao;
import com.increff.Dao.UserDao;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.Channel;
import com.increff.Model.ChannelListing;
import com.increff.Service.impl.ChannelServiceImpl;
import com.increff.Util.TestUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.notNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChannelServiceTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    UserDao userDao;
    @InjectMocks
    ChannelServiceImpl channelService;
    @Mock
    private ChannelDao channelDao;
    @Mock
    private ProductDao productDao;
    
    @Test
    public void addChannel_Success() {
        when(channelDao.checkChannelExistOrNot((String) notNull())).thenReturn(Optional.empty());
        when(channelDao.saveChannel(anyObject())).thenReturn(TestUtil.getChannel());
        Channel channel = channelService.addChannel(TestUtil.getChannelDto());
        assertNotNull(channel);
        assertEquals(channel.getName(), "MockName");
    }
    
    @Test(expected = ApiGenericException.class)
    public void addChannel_Failure() {
        when(channelDao.checkChannelExistOrNot((String) notNull())).thenReturn(Optional.of(TestUtil.getChannel()));
        channelService.addChannel(TestUtil.getChannelDto());
    }
    
    @Test
    public void addChannelListing_Success() throws IOException {
        when(userDao.getUserByNameAndType((String) notNull(), any())).thenReturn(Optional.of(TestUtil.getUserClient()));
        when(channelDao.checkChannelExistOrNot((String) notNull())).thenReturn(Optional.of(TestUtil.getChannel()));
        when(productDao.checkProductExistByClientIdAndClientSkuId((Long) notNull(), (String) notNull()))
                .thenReturn(TestUtil.getProductList().get(0));
        when(productDao.getGlobalIdForProductByClientIdAndClientSkuId((Long) notNull(), (String) notNull())).thenReturn(1L);
        
        when(channelDao.saveChannelsListing(any())).thenReturn(TestUtil.getChannelListingList());
        File file = new File("src/test/resources/channelListing - Sheet1.txt");
        FileInputStream fis = new FileInputStream(file);
//        MockMultipartFile mfile = new MockMultipartFile("data", "filename.csv", "text/plain", "some csv".getBytes());
        MockMultipartFile mfile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(fis));
        List<ChannelListing> res = channelService.addChannelListings("Client", "MockChannel", mfile);
        
        assertNotNull(channelService.addChannelListings("Client", "MockChannel", mfile));
        
    }
    
    @Test
    public void addChannelListing_Success2() throws IOException {
        when(userDao.getUserByNameAndType((String) notNull(), any())).thenReturn(Optional.of(TestUtil.getUserClient()));
        when(channelDao.checkChannelExistOrNot((String) notNull())).thenReturn(Optional.of(TestUtil.getChannel()));
        when(productDao.checkProductExistByClientIdAndClientSkuId((Long) notNull(), (String) notNull()))
                .thenReturn(TestUtil.getProductList().get(1));
//        when(productDao.getGlobalIdForProductByClientIdAndClientSkuId((Long) notNull(), (String) notNull())).thenReturn(1L);
        
        when(channelDao.saveChannelsListing(any())).thenReturn(Collections.emptyList());
        File file = new File("src/test/resources/channelListing - Sheet1.txt");
        FileInputStream fis = new FileInputStream(file);
//        MockMultipartFile mfile = new MockMultipartFile("data", "filename.csv", "text/plain", "some csv".getBytes());
        MockMultipartFile mfile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(fis));
        List<ChannelListing> res = channelService.addChannelListings("Client", "MockChannel", mfile);
        
        assertNotNull(res);
        assertEquals(2, res.size());
        
    }
    
    @Test(expected = ApiGenericException.class)
    public void addChannelListing_Fail1() throws IOException, ApiGenericException {
        when(userDao.getUserByNameAndType((String) notNull(), any())).thenReturn(Optional.empty());
        File file = new File("src/test/resources/channelListing - Sheet1.txt");
        FileInputStream fis = new FileInputStream(file);
//        MockMultipartFile mfile = new MockMultipartFile("data", "filename.csv", "text/plain", "some csv".getBytes());
        MockMultipartFile mfile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(fis));
        List<ChannelListing> res = channelService.addChannelListings("Client", "MockChannel", mfile);
        thrown.expect(ApiGenericException.class);
        thrown.expectMessage("Client doesn't exist");
        channelService.addChannelListings("Client", "MockChannel", mfile);
        
    }
    
    
    @Test(expected = ApiGenericException.class)
    public void addChannelListing_Fail2() throws IOException {
        when(userDao.getUserByNameAndType((String) notNull(), any())).thenReturn(Optional.of(TestUtil.getUserClient()));
        when(channelDao.checkChannelExistOrNot((String) notNull())).thenReturn(Optional.empty());
        File file = new File("src/test/resources/channelListing - Sheet1.txt");
        FileInputStream fis = new FileInputStream(file);
//        MockMultipartFile mfile = new MockMultipartFile("data", "filename.csv", "text/plain", "some csv".getBytes());
        MockMultipartFile mfile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(fis));
        List<ChannelListing> res = channelService.addChannelListings("Client", "MockChannel", mfile);
        
        channelService.addChannelListings("Client", "MockChannel", mfile);
        
    }
}

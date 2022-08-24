package com.increff.Service;

import com.increff.Dao.*;
import com.increff.Dto.OrderChannelRequestDto;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.OrderItem;
import com.increff.Service.impl.BinServiceImpl;
import com.increff.Service.impl.OrderServiceImpl;
import com.increff.Util.TestUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {
    
    @Mock
    private UserDao userDao;
    @Mock
    private ChannelDao channelDao;
    @Mock
    private OrderDao orderDao;
    @Mock
    private ProductDao productDao;
    
    @Mock
    private OrderItemDao orderItemDao;
    @Mock
    private InventoryDao inventoryDao;
    @Mock
    private BinServiceImpl binService;
    @Mock
    private BinDao binDao;
    @InjectMocks
    private OrderServiceImpl orderService;
    
    @Test
    public void createInternalChannelOrderTest_Success() throws IOException {
        when(userDao.findUserById(anyLong())).thenReturn(TestUtil.getUserCustomer()).thenReturn(TestUtil.getUserClient());
        when(channelDao.getChannelIdByNameAndType(anyString(), any())).thenReturn(1L);
        when(orderDao.addOrder(any())).thenReturn(TestUtil.getOrders().get(0));
        when(channelDao.getGlobalSkuIDByChannelIdAndSkuId(anyLong(), anyLong(), anyString())).thenReturn(101L);
        when(productDao.findMrpByGlobalSkuID(anyLong())).thenReturn(100.00);
        File file = new File("src/test/resources/orderItems.txt");
        FileInputStream fis = new FileInputStream(file);
        MockMultipartFile mockfile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(fis));
        
        List<OrderItem> res = orderService.createOrderInternalChannel(2l, 1l, "chn01", mockfile);
        assertNotNull(res);
    }
    
    @Test
    public void createOrderExternalChannelTest_Success1() {
        when(userDao.findUserById(anyLong())).thenReturn(TestUtil.getUserCustomer());
        when(userDao.findUserById(anyLong())).thenReturn(TestUtil.getUserClient());
        when(channelDao.checkChannelExistOrNot(anyString())).thenReturn(Optional.of(TestUtil.getChannel()));
        when(orderDao.addOrder(any())).thenReturn(TestUtil.getOrders().get(0));
        when(channelDao.getGlobalSkuIDByChannelIdAndSkuId(anyLong(), anyLong(), anyString())).thenReturn(1L);
        when(orderItemDao.addOrderItems(anyList())).thenReturn(TestUtil.getOrderItems());
        OrderChannelRequestDto orderChannelRequestDto = OrderChannelRequestDto.builder().clientId(1L)
                .channelName("Myntra").channelOrderId("Chn01").customerId(2L)
                .orderItems(TestUtil.getOrderItemCsvDto()).build();
        List<OrderItem> res = orderService.createOrderExternalChannel(orderChannelRequestDto);
        assertNotNull(res);
        assertEquals(res.size(), 2);
        
    }
    
    @Test(expected = ApiGenericException.class)
    public void createOrderExternalChannelTest_fail1() {
        when(userDao.findUserById(anyLong())).thenReturn(TestUtil.getUserCustomer());
        when(userDao.findUserById(anyLong())).thenReturn(TestUtil.getUserClient());
        when(channelDao.checkChannelExistOrNot(anyString())).thenReturn(Optional.empty());
        OrderChannelRequestDto orderChannelRequestDto = OrderChannelRequestDto.builder().clientId(1L)
                .channelName("Myntra").channelOrderId("Chn01").customerId(2L)
                .orderItems(TestUtil.getOrderItemCsvDto()).build();
        List<OrderItem> res = orderService.createOrderExternalChannel(orderChannelRequestDto);
        
    }
    
    @Test(expected = ApiGenericException.class)
    public void createOrderExternalChannelTest_fail2() {
        when(userDao.findUserById(anyLong())).thenReturn(TestUtil.getUserCustomer());
        when(userDao.findUserById(anyLong())).thenReturn(TestUtil.getUserCustomer());
        OrderChannelRequestDto orderChannelRequestDto = OrderChannelRequestDto.builder().clientId(1L)
                .channelName("Myntra").channelOrderId("Chn01").customerId(2L)
                .orderItems(TestUtil.getOrderItemCsvDto()).build();
        List<OrderItem> res = orderService.createOrderExternalChannel(orderChannelRequestDto);
        
    }
    
    @Test(expected = ApiGenericException.class)
    public void createOrderExternalChannelTest_fail3() {
        when(userDao.findUserById(anyLong())).thenReturn(null);
        OrderChannelRequestDto orderChannelRequestDto = OrderChannelRequestDto.builder().clientId(1L)
                .channelName("Myntra").channelOrderId("Chn01").customerId(2L)
                .orderItems(TestUtil.getOrderItemCsvDto()).build();
        List<OrderItem> res = orderService.createOrderExternalChannel(orderChannelRequestDto);
        
    }
    
    @Test
    public void orderAllocationTest_Success() {
        BinServiceImpl bin = mock(BinServiceImpl.class);
        when(orderDao.findOrderByOrderId(anyLong())).thenReturn(TestUtil.getOrders().get(0));
        when(orderItemDao.fetchOrderItemByOrderId(anyLong())).thenReturn(TestUtil.getOrderItems());
        when(inventoryDao.getInvetoryBySkuId(anyLong())).thenReturn(TestUtil.getInventoryDataList().get(0));
        when(orderItemDao.addSingleOrderItem(any())).thenReturn(TestUtil.getOrderItems().get(0));
        doCallRealMethod().when(bin).removeProductsFromBinAfterAllocation(anyLong(), anyLong());
        when(binDao.getAllBinsContainingProductBySku(anyLong())).thenReturn(TestUtil.getBinSkuDataList());
        when(binDao.uploadBinDataInventory(any())).thenReturn(TestUtil.getBinSkuDataList());
        when(orderDao.addOrder(any())).thenReturn(TestUtil.getOrders().get(0));
        List<OrderItem> res = orderService.allocateOrderPerId(1L);
        assertNotNull(res);
        assertEquals(res.size(), 2);
        
    }
    
    @Test
    public void orderAllocationTest_Success2() {
        BinServiceImpl bin = mock(BinServiceImpl.class);
        when(orderDao.findOrderByOrderId(anyLong())).thenReturn(TestUtil.getOrders().get(0));
        when(orderItemDao.fetchOrderItemByOrderId(anyLong())).thenReturn(TestUtil.getOrderItems());
        when(inventoryDao.getInvetoryBySkuId(anyLong())).thenReturn(TestUtil.getInventoryDataList().get(2));
        when(orderItemDao.addSingleOrderItem(any())).thenReturn(TestUtil.getOrderItems().get(0));
        doCallRealMethod().when(bin).removeProductsFromBinAfterAllocation(anyLong(), anyLong());
        when(binDao.getAllBinsContainingProductBySku(anyLong())).thenReturn(TestUtil.getBinSkuDataList());
        when(binDao.uploadBinDataInventory(any())).thenReturn(TestUtil.getBinSkuDataList());
        when(orderDao.addOrder(any())).thenReturn(TestUtil.getOrders().get(0));
        List<OrderItem> res = orderService.allocateOrderPerId(1L);
        assertNotNull(res);
        assertEquals(res.size(), 2);
        
    }
    
    @Test(expected = ApiGenericException.class)
    public void orderAllocationTest_Fail1() {
        when(orderDao.findOrderByOrderId(anyLong())).thenReturn(null);
        List<OrderItem> res = orderService.allocateOrderPerId(1L);
    }
    
    @Test(expected = ApiGenericException.class)
    public void orderAllocationTest_Fail2() {
        BinServiceImpl bin = mock(BinServiceImpl.class);
        when(orderDao.findOrderByOrderId(anyLong())).thenReturn(TestUtil.getOrders().get(1));
        List<OrderItem> res = orderService.allocateOrderPerId(1L);
    }
}

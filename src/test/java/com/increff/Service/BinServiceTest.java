package com.increff.Service;

import com.increff.Dao.BinDao;
import com.increff.Dao.InventoryDao;
import com.increff.Dao.ProductDao;
import com.increff.Dto.Converter.BinConverter;
import com.increff.Model.BinSku;
import com.increff.Service.impl.BinServiceImpl;
import com.increff.Util.TestUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
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
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BinServiceTest {
    
    Random rand;
    @Mock
    private BinDao binDao;
    @Mock
    private BinConverter binConverter;
    @Mock
    private ProductDao productDao;
    @Mock
    private InventoryDao inventoryDao;
    @InjectMocks
    private BinServiceImpl binService;
    
    @Before
    public void setUp() {
        rand = new Random();
    }
    
    @Test
    public void uploadBinDataTest_Success() {
        when(binDao.getAllBinIds()).thenReturn(TestUtil.getAllIds());
        when(productDao.getGlobalSkuIds()).thenReturn(TestUtil.getAllIds());
        when(binDao.getBinEntityByBinIdAndSkuId(anyLong(), anyLong())).
                thenReturn(TestUtil.getBinSkuDataList().get(0));
        
        when(binConverter.convertBinSkuDtoToBin(any())).thenCallRealMethod();
        when(inventoryDao.getInvetoryBySkuId(anyLong())).thenReturn(TestUtil.getInventoryDataList().get(0));
        when(inventoryDao.updateInventoryEntity(any())).thenReturn(TestUtil.getInventoryDataList().get(1));
        when(binDao.uploadBinDataInventory(any())).thenReturn(TestUtil.getBinSkuDataList());
        MockMultipartFile mockfile = null;
        try {
            File file = new File("src/test/resources/BinSkuData.txt");
            FileInputStream fis = new FileInputStream(file);
            mockfile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(fis));
        } catch (IOException e) {
            e.getCause();
        }
        List<BinSku> res = binService.uploadBinData(mockfile);
        assertNotNull(res);
        
    }
    
    @Test
    public void uploadBinDataTest_Success2() throws IOException {
        when(binDao.getAllBinIds()).thenReturn(TestUtil.getAllIds());
        when(productDao.getGlobalSkuIds()).thenReturn(TestUtil.getAllIds());
        when(binDao.getBinEntityByBinIdAndSkuId(anyLong(), anyLong())).
                thenReturn(null);
        when(binConverter.convertBinSkuDtoToBin(any())).thenCallRealMethod();
        when(inventoryDao.getInvetoryBySkuId(anyLong())).thenReturn(null);
        when(inventoryDao.updateInventoryEntity(any())).thenReturn(TestUtil.getInventoryDataList().get(1));
        when(binDao.uploadBinDataInventory(any())).thenReturn(TestUtil.getBinSkuDataList());
        File file = new File("src/test/resources/BinSkuData.txt");
        FileInputStream fis = new FileInputStream(file);
        MockMultipartFile mockfile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(fis));
        
        List<BinSku> res = binService.uploadBinData(mockfile);
        assertNotNull(res);
        
    }
    
    @Test
    public void uploadBinDataTest_Fail1() throws IOException {
        when(binDao.getAllBinIds()).thenReturn(TestUtil.getAllIds());
        when(productDao.getGlobalSkuIds()).thenReturn(TestUtil.getAllIds());
        when(binConverter.convertBinSkuDtoToBin(any())).thenCallRealMethod();
        when(binDao.uploadBinDataInventory(any())).thenReturn(Collections.emptyList());
        
        MockMultipartFile mockfile = new MockMultipartFile("data", "filename.csv", "text/plain", "some csv".getBytes());
        
        List<BinSku> res = binService.uploadBinData(mockfile);
        assertEquals(res.size(), 0);
        assertNotNull(res);
        
    }
    
    @Test
    public void removeProductsFromBinAfterAllocationTest_Success() {
        when(binDao.getAllBinsContainingProductBySku(anyLong())).thenReturn(TestUtil.getBinSkuDataList());
        when(binDao.uploadBinDataInventory(any())).thenReturn(TestUtil.getBinSkuDataList());
        binService.removeProductsFromBinAfterAllocation(101L, 5l);
//          verify();
    
    }
}

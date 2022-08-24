package com.increff.Service;

import com.increff.Dao.ProductDao;
import com.increff.Dao.UserDao;
import com.increff.Dto.Converter.ProductConverter;
import com.increff.Exception.ApiGenericException;
import com.increff.Service.impl.ProductServiceImpl;
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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.notNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {
    
    @Mock
    private UserDao userDao;
    
    @Mock
    private ProductDao productDao;
    @Mock
    private ProductConverter converter;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    @Test
    public void uploadProductDetailsForClient_Success() throws IOException {
        when(userDao.findUserById((Long) notNull())).thenReturn(TestUtil.getUserClient());
        when(productDao.getClientSkuIdByClientId(anyLong())).thenReturn(TestUtil.getClientSkuIdsList());
//        doCallRealMethod().when(converter).productDtoToProductBulk(anyLong(), any());
        when(converter.productDtoToProductBulk((Long) notNull(), any())).thenReturn(TestUtil.getProductList());
        when(productDao.getGlobalIdForProductByClientIdAndClientSkuId((Long) notNull(),
                (String) notNull())).thenReturn(1L);
        when(productDao.updateProductsDataForClient(any())).thenReturn(TestUtil.getProductList().get(0));
        File file = new File("src/test/resources/ProductList.txt");
        FileInputStream fis = new FileInputStream(file);
//        MockMultipartFile mfile = new MockMultipartFile("data", "filename.csv", "text/plain", "some csv".getBytes());
        MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(fis));
        assertNotNull(productService.uploadProductDetailsForClient(1L, multipartFile));
        
    }
    
    @Test(expected = ApiGenericException.class)
    public void uploadProductDetailsForClient_Fail1() throws IOException {
        when(userDao.findUserById((Long) notNull())).thenReturn(null);
        File file = new File("src/test/resources/ProductList.txt");
        FileInputStream fis = new FileInputStream(file);
//        MockMultipartFile mfile = new MockMultipartFile("data", "filename.csv", "text/plain", "some csv".getBytes());
        MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(fis));
        productService.uploadProductDetailsForClient(1L, multipartFile);
        
    }
    
    @Test(expected = ApiGenericException.class)
    public void uploadProductDetailsForClient_fail2() throws IOException {
        when(userDao.findUserById((Long) notNull())).thenReturn(TestUtil.getUserCustomer());
        when(productDao.getClientSkuIdByClientId(anyLong())).thenReturn(TestUtil.getClientSkuIdsList());
        when(converter.productDtoToProductBulk((Long) notNull(), any())).thenReturn(TestUtil.getProductList());
        when(productDao.getGlobalIdForProductByClientIdAndClientSkuId((Long) notNull(),
                (String) notNull())).thenReturn(1L);
        when(productDao.updateProductsDataForClient(any())).thenReturn(TestUtil.getProductList().get(0));
        File file = new File("src/test/resources/ProductList.txt");
        FileInputStream fis = new FileInputStream(file);
//        MockMultipartFile mfile = new MockMultipartFile("data", "filename.csv", "text/plain", "some csv".getBytes());
        MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(fis));
        assertNotNull(productService.uploadProductDetailsForClient(1L, multipartFile));
        
    }
}

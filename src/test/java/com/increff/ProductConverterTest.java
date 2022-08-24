package com.increff;

import com.increff.Dto.Converter.ProductConverter;
import com.increff.Dto.ProductDto;
import com.increff.Model.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ProductConverterTest {

    @InjectMocks
    private ProductConverter productConverter;

    @Test
    public void productDtoToProductBulkTest() {
        List<ProductDto> productDtoList = new LinkedList<>();
        ProductDto prod1 = new ProductDto("sku01", "product1", "brd1", 123.02, "poor");

        ProductDto prod2 = new ProductDto("sku02", "product2", "brd2", 153.02, "good");
        productDtoList.add(prod1);
        productDtoList.add(prod2);
        List<Product> res = productConverter.productDtoToProductBulk(1L, productDtoList);
        assertNotNull(res);
        assertEquals(res.size(), 2);
        assertEquals(res.get(0).getClientId(), new Long(1));
    }
}

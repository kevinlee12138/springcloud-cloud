package com.kevin.product.service;


import com.kevin.product.CloudProductApplicationTests;
import com.kevin.product.dataObject.ProductInfo;
import com.kevin.product.dto.DecreaseStockInput;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 廖师兄
 * 2017-12-09 22:03
 */
@Component
public class ProductServiceTest extends CloudProductApplicationTests {

    @Autowired
    private ProductService productService;

    @Test
    public void findUpAll() throws Exception {
        List<ProductInfo> list = productService.findUpAll();
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void decreaseStock() throws Exception {
        DecreaseStockInput decreaseStockInput = new DecreaseStockInput("157875196366160022",1);
        productService.decreaseStock(Arrays.asList(decreaseStockInput));
    }


}
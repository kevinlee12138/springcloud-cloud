package com.kevin.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cloud-product")
public interface ProductClient {

    @GetMapping("/msg")
    String getMsg();
}

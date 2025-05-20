package com.yizhoucp.de.openfeign.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "open-feign-test")
public interface TestFeignClient {

    @GetMapping("/api/get-header")
    String curEnv(@RequestParam("key") String key);
}

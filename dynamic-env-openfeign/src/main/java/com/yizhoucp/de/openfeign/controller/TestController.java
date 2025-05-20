package com.yizhoucp.de.openfeign.controller;

import com.yizhoucp.de.openfeign.remote.TestFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class TestController {

    @Resource
    private TestFeignClient testFeignClient;

    @GetMapping("/api/get-header")
    public String curEnv(HttpServletRequest request, String key) {
        log.info("header key: {}, header: {}", key, request.getHeader(key));
        return request.getHeader(key);
    }

    @GetMapping("/api/get-remote-header")
    public String getRemoteHeader(HttpServletRequest request, String key) {
        log.info("source header key value: {}", request.getHeader(key));
        return testFeignClient.curEnv(key);
    }
}

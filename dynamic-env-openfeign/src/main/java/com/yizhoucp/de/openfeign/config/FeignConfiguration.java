package com.yizhoucp.de.openfeign.config;

import com.yizhoucp.de.openfeign.util.EnvUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfiguration implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String envMark = request.getHeader("ali-env-mark");
            if (StringUtils.hasLength(envMark)) {
                requestTemplate.header("ali-env-mark", envMark);
            }
        }
    }
}
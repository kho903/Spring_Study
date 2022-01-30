package com.example.jpa.common.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CommonInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("#################################################");
        log.info("[인터셉터] - preHandler 스타트");
        log.info("#################################################");
        log.info(request.getMethod());
        log.info(request.getRequestURI());

        return true;
    }
}

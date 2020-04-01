package com.standard.framework.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhy
 * Date: 2020/3/30
 * Time: 15:05
 * Description: 过滤器demo
 */
@Slf4j
public class MyTestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        log.info("【this is MyTestFilter】【url】{}", request.getRequestURI());
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

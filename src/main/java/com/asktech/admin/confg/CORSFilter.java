package com.asktech.admin.confg;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class CORSFilter implements Filter {


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        @SuppressWarnings("unused")
        HttpServletRequest request = (HttpServletRequest) req;
        
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "OPTIONS,GET,PUT,DELETE,POST");
        response.setHeader("Access-Control-Max-Age", "600");
        response.setHeader("Access-Control-Allow-Headers", "*");

        chain.doFilter(req, res);

    }

    @Override
    public void init(FilterConfig arg0) {

    }

    @Override
    public void destroy() {

    }

}

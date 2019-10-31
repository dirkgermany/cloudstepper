package com.dam.stock.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TestConf implements WebMvcConfigurer {
 
    @Autowired
    private StockBla stockBla;
 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(stockBla)
          .addPathPatterns("/**/getStockHistory/**/");
    }
}
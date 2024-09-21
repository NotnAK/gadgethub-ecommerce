package com.example.ecommerce;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/home.html");
        registry.addViewController("/products").setViewName("forward:/products.html");
        registry.addViewController("/profile").setViewName("forward:/profile.html");
        registry.addViewController("/admin").setViewName("forward:/admin/admin.html");
        registry.addViewController("/product").setViewName("forward:/productPage.html");
        registry.addViewController("/login").setViewName("forward:/login.html");
        registry.addViewController("/register").setViewName("forward:/register.html");
        registry.addViewController("/logout").setViewName("forward:/logout.html");
        registry.addViewController("/admin/add-product").setViewName("forward:/admin/addItem.html");
        registry.addViewController("/admin/manage").setViewName("forward:/admin/manage.html");
        registry.addViewController("/admin/edit").setViewName("forward:/admin/edit.html");
    }
}

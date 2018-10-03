package com.secondthorn.solitaire.pyramid.service.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This configurer forwards users going to /pyramid-solitaire/solver to the
 * resources/static/pyramid-solitaire/solver/index.html
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/pyramid-solitaire/solver").setViewName("redirect:/pyramid-solitaire/solver/");
        registry.addViewController("/pyramid-solitaire/solver/").setViewName("forward:/pyramid-solitaire/solver/index.html");
    }
}

package com.openelements.issues;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// SPDX-License-Identifier: Apache-2.0

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    /**
     * Logger for this class.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Default constructor.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui/index.html");
    }

    /**
     * Configure CORS settings for the application.
     *
     * @param registry the CorsRegistry to configure
     */
    @Override
    public void addCorsMappings(@NonNull final CorsRegistry registry) {
        log.info("Configuring CORS mappings");
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}

package com.openelements.issues;

import com.openelements.issues.config.IssueServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(IssueServiceProperties.class)
public class IssueServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IssueServiceApplication.class, args);
    }
}

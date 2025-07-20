package com.pworx.kafkaconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Application entry point.  It enables Spring Boot auto configuration and
 * retry support.  Exposing a single class with a {@code main} method keeps the
 * bootstrap logic minimal while delegating configuration to Spring.
 */
@SpringBootApplication
@EnableRetry
public class Application {

    /**
     * Starts the Spring Boot application.  Using {@link SpringApplication#run}
     * ensures that the entire context is created once so other beans can be
     * wired together by dependency injection.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

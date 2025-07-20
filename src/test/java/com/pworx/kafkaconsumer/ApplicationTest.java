package com.pworx.kafkaconsumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationTest {

    @Test
    void applicationIsAnnotated() {
        assertNotNull(Application.class.getAnnotation(SpringBootApplication.class));
    }
}

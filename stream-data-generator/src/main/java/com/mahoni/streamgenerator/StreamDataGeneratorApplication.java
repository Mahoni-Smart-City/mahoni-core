package com.mahoni.streamgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StreamDataGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(StreamDataGeneratorApplication.class, args);
    }
}

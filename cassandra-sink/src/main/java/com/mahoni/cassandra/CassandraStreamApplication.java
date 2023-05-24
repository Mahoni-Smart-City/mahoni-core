package com.mahoni.cassandra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class CassandraStreamApplication {
    public static void main(String[] args) {
        SpringApplication.run(CassandraStreamApplication.class, args);
    }
}

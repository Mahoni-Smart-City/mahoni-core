package com.mahoni.tripservice.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {

  @Bean("objectMapper")
  ObjectMapper objectMapper() {
    return new ObjectMapper().registerModule(new JavaTimeModule());
  }
}

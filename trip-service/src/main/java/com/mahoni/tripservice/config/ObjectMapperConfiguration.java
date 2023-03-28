package com.mahoni.tripservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {

  @Bean
  ObjectMapper objectMapper() {
    return new ObjectMapper().registerModule(new Jdk8Module());
  }
}

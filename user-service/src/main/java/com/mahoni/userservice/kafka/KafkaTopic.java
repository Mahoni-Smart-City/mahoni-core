package com.mahoni.userservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Configuration
@EnableKafka
public class KafkaTopic {

  static String USER_POINT_COMPACTED_TOPIC = "user-point-compacted-topic";
  static String USER_POINT_TOPIC = "user-point-topic";

  @Bean
  public NewTopic userPointCompacted() {
    return TopicBuilder
      .name(USER_POINT_COMPACTED_TOPIC)
      .replicas(3)
      .partitions(3)
      .compact()
      .build();
  }

  @Bean
  public NewTopic userPoint() {
    return TopicBuilder
      .name(USER_POINT_TOPIC)
      .replicas(3)
      .partitions(3)
      .build();
  }
}

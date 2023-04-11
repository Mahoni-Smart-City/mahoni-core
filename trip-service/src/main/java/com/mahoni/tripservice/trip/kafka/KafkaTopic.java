package com.mahoni.tripservice.trip.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

  static String TRIP_COMPLETED_TOPIC = "trip-completed-topic";

  @Bean
  public NewTopic tripCompletedTopic() {
    return TopicBuilder
      .name(TRIP_COMPLETED_TOPIC)
      .replicas(3)
      .partitions(3)
      .build();
  }
}

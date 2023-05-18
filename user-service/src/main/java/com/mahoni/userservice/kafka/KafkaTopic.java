package com.mahoni.userservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaTopic {

  public static String USER_POINT_COMPACTED_TOPIC = "user-point-compacted-topic";
  public static String USER_POINT_TOPIC = "user-point-topic";
  public static String VOUCHER_REDEEMED_TOPIC = "voucher-redeemed-topic";
  public static String TRIP_TOPIC = "trip-topic";
  public static String AIR_QUALITY_RAW = "air-quality-raw-topic";
  public static String AIR_QUALITY_PROCESSED = "air-quality-processed-topic";
  public static String AIR_QUALITY_COMPACTED = "air-quality-compacted-topic";


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
  // All topics are created on a single service for easier initialization
  @Bean
  public NewTopic tripCompletedTopic() {
    return TopicBuilder
      .name(TRIP_TOPIC)
      .replicas(3)
      .partitions(3)
      .build();
  }

  @Bean
  public NewTopic voucherRedeemedTopic() {
    return TopicBuilder
      .name(VOUCHER_REDEEMED_TOPIC)
      .replicas(3)
      .partitions(3)
      .build();
  }

  @Bean
  public NewTopic airQualityRaw() {
    return TopicBuilder
      .name(AIR_QUALITY_RAW)
      .replicas(3)
      .partitions(3)
      .build();
  }

  @Bean
  public NewTopic airQualityProcessed() {
    return TopicBuilder
      .name(AIR_QUALITY_PROCESSED)
      .replicas(3)
      .partitions(3)
      .build();
  }

  @Bean
  public NewTopic airQualityCompacted() {
    return TopicBuilder
      .name(AIR_QUALITY_COMPACTED)
      .replicas(3)
      .partitions(3)
      .compact()
      .build();
  }
}

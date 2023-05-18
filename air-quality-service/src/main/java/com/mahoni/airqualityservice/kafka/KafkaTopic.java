package com.mahoni.airqualityservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class KafkaTopic {

  public static String AIR_QUALITY_RAW = "air-quality-raw-topic";
  public static String AIR_QUALITY_PROCESSED = "air-quality-processed-topic";
  public static String AIR_QUALITY_COMPACTED = "air-quality-compacted-topic";

//  @Bean
//  public NewTopic airQualityRaw() {
//    return TopicBuilder
//      .name(AIR_QUALITY_RAW)
//      .replicas(3)
//      .partitions(3)
//      .build();
//  }
//
//  @Bean
//  public NewTopic airQualityProcessed() {
//    return TopicBuilder
//      .name(AIR_QUALITY_PROCESSED)
//      .replicas(3)
//      .partitions(3)
//      .build();
//  }
//  @Bean
//  public NewTopic airQualityProcessed() {
//    return TopicBuilder
//      .name(AIR_QUALITY_COMPACTED)
//      .replicas(3)
//      .partitions(3)
//      .compact()
//      .build();
//  }
}

package com.mahoni.voucherservice.voucher.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaTopic {

  static String VOUCHER_REDEEMED_TOPIC = "voucher-redeemed-topic";

//  @Bean
//  public NewTopic voucherRedeemedTopic() {
//    return TopicBuilder
//      .name(VOUCHER_REDEEMED_TOPIC)
//      .replicas(3)
//      .partitions(3)
//      .build();
//  }
}

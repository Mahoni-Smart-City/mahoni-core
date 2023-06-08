package com.mahoni.loyaltyservice.kafka;

import com.mahoni.loyaltyservice.repository.LoyaltyRepository;
import com.mahoni.loyaltyservice.service.LoyaltyService;
import com.mahoni.schema.UserPointSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
public class LoyaltyEventListener {
  @Autowired
  LoyaltyService loyaltyService;

  @KafkaListener( topics = "user-point-topic", groupId = "loyalty-service-group-id", containerFactory = "kafkaListenerContainerFactory")
  public void consumeTrip(ConsumerRecord<String, UserPointSchema> record) {
    log.info("Received event: " + record.value());
    UserPointSchema userPoint = record.value();
    loyaltyService.createOrUpdatePoint(userPoint);
  }
}

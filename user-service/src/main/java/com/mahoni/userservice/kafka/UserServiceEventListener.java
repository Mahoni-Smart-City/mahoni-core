package com.mahoni.userservice.kafka;

import com.mahoni.schema.TripCompletedSchema;
import com.mahoni.schema.VoucherRedeemedSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class UserServiceEventListener {

  @KafkaListener( topics = "trip-completed-topic", groupId = "user-service-group-id", containerFactory = "kafkaListenerContainerFactory")
  public void consumeTripCompleted(ConsumerRecord<String, TripCompletedSchema> record) {
    System.out.println("Consumed message -> " + record.key());
    System.out.println("Message payload -> " + record.value());
  }

  @KafkaListener( topics = "voucher-redeemed-topic", groupId = "user-service-group-id", containerFactory = "kafkaListenerContainerFactory")
  public void consumeVoucherRedeemed(ConsumerRecord<String, VoucherRedeemedSchema> record) {
    System.out.println("Consumed message -> " + record.key());
    System.out.println("Message payload -> " + record.value());
  }
}
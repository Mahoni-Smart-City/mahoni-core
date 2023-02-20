package com.mahoni.userservice.kafka;

import com.mahoni.schema.UserEventSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SampleConsumer {

  @KafkaListener( topics = "test-topic", groupId = "sample-consumer", containerFactory = "kafkaListenerContainerFactory")
  public void consume(ConsumerRecord<String, UserEventSchema> record) {
    System.out.println("Consumed message -> " + record.key());
    System.out.println("Message payload -> " + record.value());
  }
}

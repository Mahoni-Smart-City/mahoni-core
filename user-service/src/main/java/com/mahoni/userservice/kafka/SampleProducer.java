package com.mahoni.userservice.kafka;

import com.mahoni.schema.UserEventType;
import com.mahoni.schema.UserSchema;
import com.mahoni.schema.UserEventSchema;
import com.mahoni.userservice.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class SampleProducer {
//
//  @Autowired
//  KafkaTemplate<String, UserEventSchema> kafkaTemplate;
//
//  final String TOPIC = "test-topic";
//
//  public void sendToKafka(User data, UserEventType eventType) {
//    ProducerRecord<String, UserEventSchema> record = createRecord(data, eventType);
//
//    try {
//      kafkaTemplate.send( record);
//    }
//    catch (Exception e) {
//      log.error(e.toString());
//    }
//  }
//
//  private ProducerRecord<String,UserEventSchema> createRecord(User data, UserEventType eventType) {
//
//    String id = UUID.randomUUID().toString();
//
//    UserSchema user = UserSchema
//      .newBuilder()
//      .setId(data.getId())
//      .setEmail(data.getEmail())
//      .setName(data.getName())
//      .setPhoneNumber(data.getPhoneNumber())
//      .build();
//
//    UserEventSchema event = UserEventSchema
//      .newBuilder()
//      .setEventId(id)
//      .setTimestamp(Instant.now().toEpochMilli())
//      .setEventType(eventType.name())
//      .setData(user)
//      .build();
//
//    return new ProducerRecord<>(TOPIC, id, event);
//  }
}

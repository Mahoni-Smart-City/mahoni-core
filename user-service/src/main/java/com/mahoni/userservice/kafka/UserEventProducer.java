package com.mahoni.userservice.kafka;

import com.mahoni.schema.UserPointSchema;
import com.mahoni.userservice.model.User;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
public class UserEventProducer {

  @Autowired
  KafkaTemplate<String, UserPointSchema> kafkaTemplate;

  public void send(User user, Integer point, String  lastModifiedBy) {

    String id = UUID.randomUUID().toString();

    UserPointSchema event = UserPointSchema.newBuilder()
      .setEventId(id)
      .setTimestamp(parseTimestamp(LocalDateTime.now()))
      .setUserId(user.getId().toString())
      .setPrevPoint(point)
      .setPoint(user.getPoint())
      .setLastModifiedBy(lastModifiedBy)
      .build();

    kafkaTemplate.send(new ProducerRecord<>(KafkaTopic.USER_POINT_TOPIC, id, event));
  }

  private long parseTimestamp(LocalDateTime dateTime) {
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }
}

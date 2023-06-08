package com.mahoni.userservice.kafka;

import com.mahoni.schema.UserPointSchema;
import com.mahoni.userservice.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static com.mahoni.userservice.kafka.KafkaTopic.USER_POINT_TOPIC_PARTITION;

@Component
@Slf4j
public class UserEventProducer {

  @Autowired
  KafkaTemplate<String, UserPointSchema> kafkaTemplate;

  public void send(User user, Integer point, String  lastModifiedBy) {

    String id = UUID.randomUUID().toString();

    UserPointSchema event = UserPointSchema.newBuilder()
      .setEventId(id)
      .setTimestamp(parseTimestamp(LocalDateTime.now()))
      .setUserId(user.getId().toString())
      .setPrevPoint(user.getPoint() - point)
      .setPoint(user.getPoint())
      .setLastModifiedBy(lastModifiedBy)
      .build();

    log.info("Sending event to " + KafkaTopic.USER_POINT_TOPIC + " with payload: " + event.toString());
    kafkaTemplate.send(new ProducerRecord<>(KafkaTopic.USER_POINT_TOPIC, partition(event.getUserId()), id, event));
  }

  private int partition(String identifier) {
    return Utils.toPositive(Utils.murmur2(identifier.getBytes())) % USER_POINT_TOPIC_PARTITION;
  }

  private long parseTimestamp(LocalDateTime dateTime) {
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }
}

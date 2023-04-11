package com.mahoni.userservice.kafka;

import com.mahoni.schema.UserPointSchema;
import com.mahoni.userservice.model.User;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class UserEventProducer {

  @Autowired
  KafkaTemplate<String, UserPointSchema> kafkaTemplate;

  public void send(User user) {

    UserPointSchema event = UserPointSchema.newBuilder()
      .setUserId(user.getId().toString())
      .setPoint(user.getPoint())
      .build();

    kafkaTemplate.send(new ProducerRecord<>(KafkaTopic.USER_POINT_COMPACTED_TOPIC, user.getId().toString(), event));
  }

  @PostConstruct
  public void populate() {
    UUID id = UUID.randomUUID();

    UserPointSchema event = UserPointSchema.newBuilder()
      .setUserId(id.toString())
      .setPoint((int) (Math.random() * 10))
      .build();

    kafkaTemplate.send(new ProducerRecord<>(KafkaTopic.USER_POINT_COMPACTED_TOPIC, id.toString(), event));
  }

}

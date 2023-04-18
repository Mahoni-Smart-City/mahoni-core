package com.mahoni.cassandra.service;

import com.mahoni.schema.Envelope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class CassandraService {

  private static final String KAFKA_TOPIC = "postgres.public.air_sensors";

  @KafkaListener(topics = KAFKA_TOPIC, groupId = "air-sensor")
  public static void test(Envelope air_sensor){
    System.out.print(air_sensor.getAfter());
  }
}

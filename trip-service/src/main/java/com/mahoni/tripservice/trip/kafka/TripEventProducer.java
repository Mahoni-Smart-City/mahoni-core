package com.mahoni.tripservice.trip.kafka;

import com.mahoni.schema.TripSchema;
import com.mahoni.tripservice.trip.model.Trip;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
@Slf4j
public class TripEventProducer {

  @Autowired
  KafkaTemplate<String, TripSchema> kafkaTemplate;

  public void send(Trip trip) {

    String id = UUID.randomUUID().toString();
    long now = System.currentTimeMillis();

    TripSchema event = TripSchema.newBuilder()
      .setEventId(id)
      .setTimestamp(now)
      .setTripId(trip.getId().toString())
      .setUserId(trip.getUserId().toString())
      .setScanInPlaceId(trip.getScanInPlaceId().getId().toString())
      .setScanInTimestamp(parseTimestamp(trip.getScanInAt()))
      .setScanOutPlaceId(trip.getScanOutPlaceId() == null ? null : trip.getScanOutPlaceId().getId().toString())
      .setScanOutTimestamp(trip.getScanOutAt() == null ? null : parseTimestamp(trip.getScanOutAt()))
      .setStatus(trip.getStatus().name())
      .setAqi(trip.getAqi() == null ? null : trip.getAqi())
      .setPoint(trip.getPoint() == null ? null : trip.getPoint())
      .build();

    log.info("Sending event to " + KafkaTopic.TRIP_TOPIC + " with payload: " + event.toString());
    kafkaTemplate.send(new ProducerRecord<>(KafkaTopic.TRIP_TOPIC, id, event));
  }

  private long parseTimestamp(LocalDateTime dateTime) {
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }
}



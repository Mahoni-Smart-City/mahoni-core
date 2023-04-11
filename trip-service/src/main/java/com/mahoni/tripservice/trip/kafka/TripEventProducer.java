package com.mahoni.tripservice.trip.kafka;

import com.mahoni.schema.TripCompletedSchema;
import com.mahoni.tripservice.trip.model.Trip;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
public class TripEventProducer {

  @Autowired
  KafkaTemplate<String, TripCompletedSchema> kafkaTemplate;

  public void send(Trip trip) {

    String id = UUID.randomUUID().toString();
    long now = System.currentTimeMillis();

    TripCompletedSchema event = TripCompletedSchema.newBuilder()
      .setEventId(id)
      .setTimestamp(now)
      .setTripId(trip.getId().toString())
      .setUserId(trip.getUserId().toString())
      .setScanInPlaceId(trip.getScanInPlaceId().getId().toString())
      .setScanInTimestamp(parseTimestamp(trip.getScanInAt()))
      .setScanOutPlaceId(trip.getScanOutPlaceId().getId().toString())
      .setScanOutTimestamp(parseTimestamp(trip.getScanOutAt()))
      .setStatus(trip.getStatus())
      .setAqi(trip.getAqi())
      .setPoint(trip.getPoint())
      .build();

      kafkaTemplate.send(new ProducerRecord<>(KafkaTopic.TRIP_COMPLETED_TOPIC, id, event));
  }

  private long parseTimestamp(LocalDateTime dateTime) {
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }
}



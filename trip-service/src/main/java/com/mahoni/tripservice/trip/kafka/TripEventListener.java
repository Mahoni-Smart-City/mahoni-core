package com.mahoni.tripservice.trip.kafka;

import com.mahoni.schema.UserPointSchema;
import com.mahoni.tripservice.trip.model.TransactionStatus;
import com.mahoni.tripservice.trip.model.Trip;
import com.mahoni.tripservice.trip.model.TripStatus;
import com.mahoni.tripservice.trip.repository.TripRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class TripEventListener {

  @Autowired
  TripRepository tripRepository;

  @KafkaListener( topics = "user-point-topic", groupId = "trip-service-group-id", containerFactory = "kafkaListenerContainerFactory")
  public void consumePoint(ConsumerRecord<String, UserPointSchema> record) {
    log.info("Received event: " + record.value());
    UserPointSchema userPoint = record.value();
    // Find trip with corresponding id
    Optional<Trip> trip = tripRepository.findById(UUID.fromString(userPoint.getLastModifiedBy()));
    if (trip.isPresent()) {
      Trip pendingTrip = trip.get();
      // Check if trip is pending and the point is correct
      if (pendingTrip.getTransactionStatus().equals(TransactionStatus.PENDING.name()) && Math.abs(userPoint.getPoint() - userPoint.getPrevPoint()) == pendingTrip.getPoint()) {
        // Update transaction status
        pendingTrip.setTransactionStatus(TransactionStatus.SUCCESS.name());
        tripRepository.save(pendingTrip);
      }
    }
  }
}

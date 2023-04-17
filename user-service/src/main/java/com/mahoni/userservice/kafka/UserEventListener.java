package com.mahoni.userservice.kafka;

import com.mahoni.schema.TripSchema;
import com.mahoni.schema.VoucherRedeemedSchema;
import com.mahoni.userservice.exception.ResourceNotFoundException;
import com.mahoni.userservice.model.User;
import com.mahoni.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
public class UserEventListener {

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserEventProducer eventProducer;

  @Transactional
  @KafkaListener( topics = "trip-topic", groupId = "user-service-group-id", containerFactory = "kafkaListenerContainerFactory")
  public void consumeTrip(ConsumerRecord<String, TripSchema> record) {
    log.info("Received event: " + record.value());
    TripSchema trip = record.value();
    if (trip.getStatus().equals("FINISHED")) {
      log.info("FINISHED TRIP");
      User user = userRepository.findById(UUID.fromString(trip.getUserId())).orElseThrow(() -> new ResourceNotFoundException(UUID.fromString(trip.getUserId())));
      user.addPoint(trip.getPoint());
      userRepository.save(user);
      eventProducer.send(user, trip.getPoint(), trip.getTripId());

    }
  }

  @Transactional
  @KafkaListener( topics = "voucher-redeemed-topic", groupId = "user-service-group-id", containerFactory = "kafkaListenerContainerFactory")
  public void consumeVoucherRedeemed(ConsumerRecord<String, VoucherRedeemedSchema> record) {
    log.info("Received event: " + record.value());
    VoucherRedeemedSchema voucher = record.value();
    User user = userRepository.findById(UUID.fromString(voucher.getUserId())).orElseThrow(() -> new ResourceNotFoundException(UUID.fromString(voucher.getUserId())));
    if (user.sufficientPoint(voucher.getPoint())) {
      user.subtractPoint(voucher.getPoint());
      userRepository.save(user);
      eventProducer.send(user, -voucher.getPoint(), voucher.getVoucherId());
    }
  }
}

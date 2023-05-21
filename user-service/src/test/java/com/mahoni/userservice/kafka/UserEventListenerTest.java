package com.mahoni.userservice.kafka;

import com.mahoni.schema.TripSchema;
import com.mahoni.schema.VoucherRedeemedSchema;
import com.mahoni.userservice.model.Sex;
import com.mahoni.userservice.model.User;
import com.mahoni.userservice.repository.UserRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@DirtiesContext
@EmbeddedKafka
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserEventListenerTest {

  private final String TRIP_TOPIC = "trip-topic";

  private final String VOUCHER_REDEEMED_TOPIC = "voucher-redeemed-topic";

  @Autowired
  KafkaTemplate<String, TripSchema> kafkaTemplateTrip;

  @Autowired
  KafkaTemplate<String, VoucherRedeemedSchema> kafkaTemplateVoucherRedeemed;

  @SpyBean
  private UserEventListener userEventListener;

  @SpyBean
  private UserEventProducer userEventProducer;

  @MockBean
  private UserRepository userRepository;

  @Test
  public void givenEmbeddedKafkaBroker_whenConsumeTrip_thenMessageReceived() {
    TripSchema tripSchema = TripSchema.newBuilder()
      .setEventId(UUID.randomUUID().toString())
      .setTimestamp(System.currentTimeMillis())
      .setTripId(UUID.randomUUID().toString())
      .setUserId(UUID.randomUUID().toString())
      .setScanInPlaceId(UUID.randomUUID().toString())
      .setScanInTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
      .setScanOutPlaceId(UUID.randomUUID().toString())
      .setScanOutTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
      .setStatus("FINISHED")
      .setAqi(5.0)
      .setPoint(0)
      .build();
    User user = new User(UUID.randomUUID(), "Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, 0);

    when(userRepository.findById(any())).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenReturn(user);
    kafkaTemplateTrip.send(new ProducerRecord<>(TRIP_TOPIC, UUID.randomUUID().toString(), tripSchema));

    verify(userEventListener, timeout(5000).times(1))
      .consumeTrip(any());
    verify(userEventProducer, timeout(5000).times(1))
      .send(any(), any(), any());
  }

  @Test
  public void givenEmbeddedKafkaBroker_whenConsumeVoucherRedeemed_thenMessageReceived() {
    VoucherRedeemedSchema voucherRedeemedSchema = VoucherRedeemedSchema.newBuilder()
      .setEventId(UUID.randomUUID().toString())
      .setTimestamp(System.currentTimeMillis())
      .setVoucherId(UUID.randomUUID().toString())
      .setUserId(UUID.randomUUID().toString())
      .setCode("Test")
      .setPoint(1)
      .setRedeemedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
      .setExpiredAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
      .build();
    User user = new User(UUID.randomUUID(), "Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, 5);

    when(userRepository.findById(any())).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenReturn(user);
    kafkaTemplateVoucherRedeemed.send(new ProducerRecord<>(VOUCHER_REDEEMED_TOPIC, UUID.randomUUID().toString(), voucherRedeemedSchema));

    verify(userEventListener, timeout(5000).times(1))
      .consumeVoucherRedeemed(any());
    verify(userEventProducer, timeout(5000).times(1))
      .send(any(), any(), any());
  }
}


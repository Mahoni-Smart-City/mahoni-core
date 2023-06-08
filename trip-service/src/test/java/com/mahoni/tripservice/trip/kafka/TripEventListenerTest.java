package com.mahoni.tripservice.trip.kafka;

import com.mahoni.schema.UserPointSchema;
import com.mahoni.tripservice.qrgenerator.model.QRGenerator;
import com.mahoni.tripservice.trip.model.TransactionStatus;
import com.mahoni.tripservice.trip.model.Trip;
import com.mahoni.tripservice.trip.model.TripStatus;
import com.mahoni.tripservice.trip.repository.TripRepository;
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
public class TripEventListenerTest {

  private final String USER_POINT_TOPIC = "user-point-topic";

  @Autowired
  KafkaTemplate<String, UserPointSchema> kafkaTemplate;

  @SpyBean
  private TripEventListener tripEventListener;

  @MockBean
  private TripRepository tripRepository;

  @Test
  public void givenEmbeddedKafkaBroker_whenConsumePoint_thenMessageReceived() {
    UserPointSchema userPointSchema = UserPointSchema.newBuilder()
      .setEventId(UUID.randomUUID().toString())
      .setTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
      .setUserId(UUID.randomUUID().toString())
      .setPrevPoint(0)
      .setPoint(5)
      .setLastModifiedBy(UUID.randomUUID().toString())
      .build();
    Trip trip = new Trip(UUID.randomUUID(), new QRGenerator(), LocalDateTime.now(), TripStatus.ACTIVE);
    trip.setTransactionStatus(TransactionStatus.PENDING);
    trip.setPoint(5);

    when(tripRepository.findById(any())).thenReturn(Optional.of(trip));
    when(tripRepository.save(any())).thenReturn(trip);
    kafkaTemplate.send(new ProducerRecord<>(USER_POINT_TOPIC, UUID.randomUUID().toString(), userPointSchema));

    verify(tripEventListener, timeout(5000).times(1))
      .consumePoint(any());
  }
}

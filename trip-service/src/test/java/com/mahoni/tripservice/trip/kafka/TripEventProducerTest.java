package com.mahoni.tripservice.trip.kafka;

import com.mahoni.schema.TripSchema;
import com.mahoni.tripservice.qrgenerator.model.QRGenerator;
import com.mahoni.tripservice.trip.mock.CustomKafkaAvroDeserializer;
import com.mahoni.tripservice.trip.model.TransactionStatus;
import com.mahoni.tripservice.trip.model.Trip;
import com.mahoni.tripservice.trip.model.TripStatus;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@DirtiesContext
@EmbeddedKafka
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TripEventProducerTest {

  private BlockingQueue<ConsumerRecord<String, TripSchema>> records;

  private KafkaMessageListenerContainer<String, TripSchema> container;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Autowired
  private TripEventProducer tripEventProducer;

  @Autowired
  KafkaTemplate<String, TripSchema> kafkaTemplate;

  @BeforeAll
  void setUp() {
    DefaultKafkaConsumerFactory<String, TripSchema> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());
    ContainerProperties containerProperties = new ContainerProperties("trip-topic");
    container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    records = new LinkedBlockingQueue<>();
    container.setupMessageListener((MessageListener<String, TripSchema>) records::add);
    container.start();
    ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
  }

  @Test
  public void givenEmbeddedKafkaBroker_whenSendEvent_thenMessageSent() throws InterruptedException {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator(id, "Test", "Test", id, id);
    LocalDateTime time = LocalDateTime.now().minusDays(1);
    Trip trip = new Trip(id, id, qrGenerator, qrGenerator, time, time, TripStatus.ACTIVE.name(), 1.0, 0, TransactionStatus.PENDING.name());

    tripEventProducer.send(trip);

    ConsumerRecord<String, TripSchema> message = records.poll(1000, TimeUnit.MILLISECONDS);
    assertNotNull(message);
    TripSchema result = message.value();
    assertNotNull(result);
    assertEquals(trip.getId(), UUID.fromString(result.getTripId()));
  }

  private Map<String, Object> getConsumerProperties() {
    return Map.of(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
      ConsumerConfig.GROUP_ID_CONFIG, "consumer",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true",
      ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "10",
      ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomKafkaAvroDeserializer.class,
      KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://not-used",
      KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true,
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
  }

  @AfterAll
  void tearDown() {
    container.stop();
  }
}

package com.mahoni.userservice.kafka;

import com.mahoni.schema.UserPointSchema;
import com.mahoni.userservice.mock.CustomKafkaAvroDeserializer;
import com.mahoni.userservice.model.Sex;
import com.mahoni.userservice.model.User;
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
public class UserEventProducerTest {

  private BlockingQueue<ConsumerRecord<String, UserPointSchema>> records;

  private KafkaMessageListenerContainer<String, UserPointSchema> container;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Autowired
  private UserEventProducer userEventProducer;

  @Autowired
  KafkaTemplate<String, UserPointSchema> kafkaTemplate;

  @BeforeAll
  void setUp() {
    DefaultKafkaConsumerFactory<String, UserPointSchema> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());
    ContainerProperties containerProperties = new ContainerProperties("user-point-topic");
    container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    records = new LinkedBlockingQueue<>();
    container.setupMessageListener((MessageListener<String, UserPointSchema>) records::add);
    container.start();
    ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
  }

  @Test
  public void givenEmbeddedKafkaBroker_whenSendEvent_thenMessageSent() throws InterruptedException {
    User user = new User(UUID.randomUUID(), "Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, 0);

    userEventProducer.send(user, 0, UUID.randomUUID().toString());

    ConsumerRecord<String, UserPointSchema> message = records.poll(500, TimeUnit.MILLISECONDS);
    assertNotNull(message);
    UserPointSchema result = message.value();
    assertNotNull(result);
    assertEquals(user.getId(), UUID.fromString(result.getUserId()));
    assertEquals(0, result.getPoint());
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

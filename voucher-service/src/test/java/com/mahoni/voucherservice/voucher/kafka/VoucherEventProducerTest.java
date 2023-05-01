package com.mahoni.voucherservice.voucher.kafka;

import com.mahoni.schema.VoucherRedeemedSchema;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.voucher.mock.CustomKafkaAvroDeserializer;
import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.model.VoucherStatus;
import com.mahoni.voucherservice.voucher.model.VoucherType;
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
public class VoucherEventProducerTest {

  private BlockingQueue<ConsumerRecord<String, VoucherRedeemedSchema>> records;

  private KafkaMessageListenerContainer<String, VoucherRedeemedSchema> container;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Autowired
  private VoucherEventProducer voucherEventProducer;

  @Autowired
  KafkaTemplate<String, VoucherRedeemedSchema> kafkaTemplate;

  @BeforeAll
  void setUp() {
    DefaultKafkaConsumerFactory<String, VoucherRedeemedSchema> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());
    ContainerProperties containerProperties = new ContainerProperties(KafkaTopic.VOUCHER_REDEEMED_TOPIC);
    container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    records = new LinkedBlockingQueue<>();
    container.setupMessageListener((MessageListener<String, VoucherRedeemedSchema>) records::add);
    container.start();
    ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
  }

  @Test
  public void givenEmbeddedKafkaBroker_whenSendEvent_thenMessageSent() throws InterruptedException {
    Voucher voucher = new Voucher(UUID.randomUUID(), "Test", "Test", VoucherType.FNB, 5, LocalDateTime.now(), LocalDateTime.now(), new Merchant(), 0);
    RedeemVoucher redeemVoucher = new RedeemVoucher(UUID.randomUUID(), voucher, UUID.randomUUID(), "Test", VoucherStatus.ACTIVE, LocalDateTime.now(),LocalDateTime.now());

    voucherEventProducer.send(redeemVoucher);

    ConsumerRecord<String, VoucherRedeemedSchema> message = records.poll(1000, TimeUnit.MILLISECONDS);
    assertNotNull(message);
    VoucherRedeemedSchema result = message.value();
    assertNotNull(result);
    assertEquals("Test", result.getCode());
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

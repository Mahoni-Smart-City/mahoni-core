package com.mahoni.voucherservice.voucher.kafka;

import com.mahoni.schema.UserPointSchema;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.model.VoucherStatus;
import com.mahoni.voucherservice.voucher.model.VoucherType;
import com.mahoni.voucherservice.voucher.repository.RedeemVoucherRepository;
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
public class VoucherEventListenerTest {

  private final String USER_POINT_TOPIC = "user-point-topic";

  @Autowired
  KafkaTemplate<String, UserPointSchema> kafkaTemplate;

  @SpyBean
  private VoucherEventListener voucherEventListener;

  @MockBean
  private RedeemVoucherRepository redeemVoucherRepository;

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
    Voucher voucher = new Voucher(UUID.randomUUID(), "Test", "Test", VoucherType.FOOD_AND_BEVERAGES, 5, LocalDateTime.now(), LocalDateTime.now(), new Merchant(), 0);
    RedeemVoucher redeemVoucher = new RedeemVoucher(voucher, "Test", LocalDateTime.now());
    redeemVoucher.setStatus(VoucherStatus.PENDING);

    when(redeemVoucherRepository.findById(any())).thenReturn(Optional.of(redeemVoucher));
    when(redeemVoucherRepository.save(any())).thenReturn(redeemVoucher);
    kafkaTemplate.send(new ProducerRecord<>(USER_POINT_TOPIC, UUID.randomUUID().toString(), userPointSchema));

    verify(voucherEventListener, timeout(5000).times(1))
      .consumePoint(any());
  }
}

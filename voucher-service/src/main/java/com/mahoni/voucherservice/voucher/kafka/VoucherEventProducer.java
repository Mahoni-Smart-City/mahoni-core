package com.mahoni.voucherservice.voucher.kafka;

import com.mahoni.schema.VoucherRedeemedSchema;
import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static com.mahoni.voucherservice.voucher.kafka.KafkaTopic.VOUCHER_REDEEMED_TOPIC_PARTITION;

@Component
@Slf4j
public class VoucherEventProducer {

  @Autowired
  KafkaTemplate<String, VoucherRedeemedSchema> kafkaTemplate;

  public void send(RedeemVoucher redeemVoucher) {

    String id = UUID.randomUUID().toString();
    long now = System.currentTimeMillis();

    VoucherRedeemedSchema event = VoucherRedeemedSchema.newBuilder()
      .setEventId(id)
      .setTimestamp(now)
      .setVoucherId(redeemVoucher.getId().toString())
      .setUserId(redeemVoucher.getUserId().toString())
      .setCode(redeemVoucher.getRedeemCode())
      .setPoint(redeemVoucher.getVoucher().getPoint())
      .setRedeemedAt(parseTimestamp(redeemVoucher.getRedeemedAt()))
      .setExpiredAt(parseTimestamp(redeemVoucher.getExpiredAt()))
      .build();

    log.info("Sending event to " + KafkaTopic.VOUCHER_REDEEMED_TOPIC + " with payload: " + event.toString());
    kafkaTemplate.send(new ProducerRecord<>(KafkaTopic.VOUCHER_REDEEMED_TOPIC, partition(event.getUserId()), id, event));
  }

  private int partition(String identifier) {
    return Utils.toPositive(Utils.murmur2(identifier.getBytes())) % VOUCHER_REDEEMED_TOPIC_PARTITION;
  }

  private long parseTimestamp(LocalDateTime dateTime) {
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }
}

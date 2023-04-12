package com.mahoni.voucherservice.voucher.kafka;

import com.mahoni.schema.UserPointSchema;
import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import com.mahoni.voucherservice.voucher.model.VoucherStatus;
import com.mahoni.voucherservice.voucher.repository.RedeemVoucherRepository;
import com.mahoni.voucherservice.voucher.repository.VoucherRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class VoucherEventListener {

  @Autowired
  VoucherRepository voucherRepository;

  @Autowired
  RedeemVoucherRepository redeemVoucherRepository;

  @KafkaListener( topics = "user-point-topic", groupId = "voucher-service-group-id", containerFactory = "kafkaListenerContainerFactory")
  public void consumePoint(ConsumerRecord<String, UserPointSchema> record) {
    log.info("Received event: " + record.value());
    UserPointSchema userPoint = record.value();
    // Find redeem voucher with corresponding id
    Optional<RedeemVoucher> redeemVoucher = redeemVoucherRepository.findById(UUID.fromString(userPoint.getLastModifiedBy()));
    if (redeemVoucher.isPresent()) {
      RedeemVoucher pendingVoucher = redeemVoucher.get();
      // Check if redeem voucher is pending and the point is correct
      if (pendingVoucher.getStatus() == VoucherStatus.PENDING && Math.abs(userPoint.getPoint() - userPoint.getPrevPoint(  )) == pendingVoucher.getVoucher().getPoint()) {
        // Update voucher status
        pendingVoucher.setStatus(VoucherStatus.ACTIVE);
        redeemVoucherRepository.save(pendingVoucher);
      }
    }
  }
}

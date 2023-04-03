package com.mahoni.voucherservice.voucher.repository;

import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RedeemVoucherRepository extends JpaRepository<RedeemVoucher, UUID> {
  @Query("SELECT a FROM RedeemVoucher a WHERE a.voucher.id = ?1 ORDER BY a.status DESC LIMIT 1")
  Optional<RedeemVoucher> findAvailableRedeemVoucherByVoucherId(UUID voucherId);

  List<RedeemVoucher> findAllByUserId(UUID uuid);

  @Query("SELECT a FROM RedeemVoucher a WHERE a.voucher.merchant.username = ?1")
  List<RedeemVoucher> findAllByMerchant(String username);
}

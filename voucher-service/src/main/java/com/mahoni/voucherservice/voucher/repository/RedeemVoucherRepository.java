package com.mahoni.voucherservice.voucher.repository;

import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import com.mahoni.voucherservice.voucher.model.VoucherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RedeemVoucherRepository extends JpaRepository<RedeemVoucher, UUID> {
  @Query("SELECT a FROM RedeemVoucher a WHERE a.voucher.id = ?1 AND a.status = null ORDER BY a.redeemCode LIMIT 1")
  Optional<RedeemVoucher> findAvailableRedeemVoucherByVoucherId(UUID voucherId);

  List<RedeemVoucher> findAllByUserId(UUID uuid);

  List<RedeemVoucher> findAllByStatus(VoucherStatus status);

  @Query("SELECT a FROM RedeemVoucher a WHERE a.voucher.merchant.username = ?1")
  List<RedeemVoucher> findAllByMerchant(String username);
}

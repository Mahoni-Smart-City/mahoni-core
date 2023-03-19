package com.mahoni.voucherservice.voucher.repository;

import com.mahoni.voucherservice.voucher.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
  Optional<Voucher> findByName(String name);
  Optional<Voucher> findByCode(String code);
}

package com.mahoni.voucherservice.voucher.repository;

import com.mahoni.voucherservice.voucher.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoucherRepository extends JpaRepository<Voucher, UUID> {
}

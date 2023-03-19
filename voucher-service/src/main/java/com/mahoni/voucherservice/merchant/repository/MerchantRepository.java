package com.mahoni.voucherservice.merchant.repository;

import com.mahoni.voucherservice.merchant.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
  Optional<Merchant> findByUsername(String username);
}

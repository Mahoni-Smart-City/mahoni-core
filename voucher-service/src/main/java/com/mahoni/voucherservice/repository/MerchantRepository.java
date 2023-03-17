package com.mahoni.voucherservice.repository;

import com.mahoni.voucherservice.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
  Optional<Merchant> findByUsername(String username);
}

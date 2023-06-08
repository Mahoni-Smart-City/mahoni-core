package com.mahoni.voucherservice.merchant.repository;

import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
  Optional<Merchant> findByUsername(String username);

  List<Merchant> findAllByRole(MerchantRole merchant);
}

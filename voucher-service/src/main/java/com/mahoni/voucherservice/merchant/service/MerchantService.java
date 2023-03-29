package com.mahoni.voucherservice.merchant.service;

import com.mahoni.voucherservice.merchant.dto.MerchantRequest;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.merchant.exception.MerchantAlreadyExistException;
import com.mahoni.voucherservice.merchant.exception.MerchantNotFoundException;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class MerchantService {

  @Autowired
  MerchantRepository merchantRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  public Merchant create(MerchantRequest merchant) {
    if (merchantRepository.findByUsername(merchant.getUsername()).isPresent()) {
      throw new MerchantAlreadyExistException(merchant.getUsername());
    }
    return merchantRepository.save(new Merchant(
      merchant.getUsername(),
      merchant.getName(),
      merchant.getEmail(),
      passwordEncoder.encode(merchant.getPassword()),
      MerchantRole.MERCHANT
    ));
  }

  public Merchant getById(UUID id) {
    Optional<Merchant> merchant = merchantRepository.findById(id);
    if (merchant.isEmpty()) {
      throw new MerchantNotFoundException(id);
    }
    return merchant.get();
  }

  public List<Merchant> getAll() {
    return merchantRepository.findAll();
  }

  public Merchant deleteById(UUID id) {
    Optional<Merchant> merchant = merchantRepository.findById(id);
    if (merchant.isEmpty()) {
      throw new MerchantNotFoundException(id);
    }
    merchantRepository.deleteById(id);
    return merchant.get();
  }

  public Merchant update(UUID id, MerchantRequest newMerchant) {
    Optional<Merchant> merchant = merchantRepository.findById(id);
    if (merchant.isEmpty()) {
      throw new MerchantNotFoundException(id);
    }
    Merchant updatedMerchant = merchant.get();
    updatedMerchant.setUsername(newMerchant.getUsername());
    updatedMerchant.setEmail(newMerchant.getEmail());
    updatedMerchant.setName(newMerchant.getName());
    updatedMerchant.setPassword(passwordEncoder.encode(newMerchant.getPassword()));
    return merchantRepository.save(updatedMerchant);
  }
}

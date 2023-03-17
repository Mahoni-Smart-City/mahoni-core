package com.mahoni.voucherservice.service;

import com.mahoni.voucherservice.dto.MerchantRequest;
import com.mahoni.voucherservice.exception.MerchantAlreadyExistException;
import com.mahoni.voucherservice.exception.MerchantNotFoundException;
import com.mahoni.voucherservice.model.Merchant;
import com.mahoni.voucherservice.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MerchantService {

  @Autowired
  MerchantRepository merchantRepository;

  @Transactional
  public Merchant create(MerchantRequest merchant) {
    if (merchantRepository.findByUsername(merchant.getUsername()).isPresent()) {
      throw new MerchantAlreadyExistException(merchant.getUsername());
    }
    return merchantRepository.save(new Merchant(merchant.getUsername(), merchant.getName(), merchant.getEmail()));
  }

  public Merchant getById(Long id) {
    Optional<Merchant> merchant = merchantRepository.findById(id);
    if (merchant.isEmpty()) {
      throw new MerchantNotFoundException(id);
    }
    return merchant.get();
  }

  public List<Merchant> getAll() {
    return merchantRepository.findAll();
  }

  @Transactional
  public Merchant deleteById(Long id) {
    Optional<Merchant> merchant = merchantRepository.findById(id);
    if (merchant.isEmpty()) {
      throw new MerchantNotFoundException(id);
    }
    merchantRepository.deleteById(id);
    return merchant.get();
  }

  @Transactional
  public Merchant update(Long id, MerchantRequest newMerchant) {
    Optional<Merchant> merchant = merchantRepository.findById(id);
    if (merchant.isEmpty()) {
      throw new MerchantNotFoundException(id);
    }
    Merchant updatedMerchant = merchant.get();
    updatedMerchant.setUsername(newMerchant.getUsername());
    updatedMerchant.setEmail(newMerchant.getEmail());
    updatedMerchant.setName(newMerchant.getName());
    return merchantRepository.save(updatedMerchant);
  }
}

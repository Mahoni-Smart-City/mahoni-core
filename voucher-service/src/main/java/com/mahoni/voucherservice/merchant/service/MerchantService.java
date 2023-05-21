package com.mahoni.voucherservice.merchant.service;

import com.mahoni.voucherservice.merchant.dto.MerchantRequest;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.merchant.exception.MerchantAlreadyExistException;
import com.mahoni.voucherservice.merchant.exception.MerchantNotFoundException;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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

  public Merchant getByUsername(String username) {
    Optional<Merchant> merchant = merchantRepository.findByUsername(username);
    if (merchant.isEmpty()) {
      throw new MerchantNotFoundException(username);
    }
    return merchant.get();
  }

  public List<Merchant> getAll() {
    if (isAdmin()) {
      return merchantRepository.findAll();
    } else {
      return merchantRepository.findAllByRole(MerchantRole.MERCHANT);
    }
  }

  public Merchant deleteById(UUID id) {
    Merchant merchant = authorizedFindMerchantById(id);
    merchantRepository.deleteById(id);
    return merchant;
  }

  public Merchant update(UUID id, MerchantRequest newMerchant) {
    Merchant updatedMerchant = authorizedFindMerchantById(id);
    updatedMerchant.setUsername(newMerchant.getUsername());
    updatedMerchant.setEmail(newMerchant.getEmail());
    updatedMerchant.setName(newMerchant.getName());
    updatedMerchant.setPassword(passwordEncoder.encode(newMerchant.getPassword()));
    return merchantRepository.save(updatedMerchant);
  }

  private boolean isAdmin() {
    return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
  }

  private boolean isAllowedMerchant(Merchant merchant) {
    return SecurityContextHolder.getContext().getAuthentication().getName().equals(
      merchant.getUsername()
    );
  }

  private Merchant authorizedFindMerchantById(UUID id) {
    Optional<Merchant> merchant = merchantRepository.findById(id);
    if (merchant.isEmpty()) {
      throw new MerchantNotFoundException(id);
    }
    if (!(isAllowedMerchant(merchant.get()) || isAdmin())) {
      throw new AccessDeniedException("You don’t have permission to access this resource");
    }
    return merchant.get();
  }
}

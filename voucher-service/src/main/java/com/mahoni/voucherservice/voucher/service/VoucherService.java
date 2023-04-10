package com.mahoni.voucherservice.voucher.service;

import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.service.MerchantService;
import com.mahoni.voucherservice.voucher.dto.VoucherRequest;
import com.mahoni.voucherservice.voucher.exception.VoucherNotFoundException;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.repository.VoucherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class VoucherService {

  @Autowired
  VoucherRepository voucherRepository;

  @Autowired
  MerchantService merchantService;

  public Voucher create(VoucherRequest voucher) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Merchant merchant = merchantService.getByUsername(auth.getName());
    Voucher newVoucher = new Voucher(
      voucher.getName(),
      voucher.getDescription(),
      voucher.getType(),
      voucher.getPoint(),
      voucher.getStartAt(),
      voucher.getExpiredAt(),
      merchant);
    newVoucher.setQuantity(0);

    return voucherRepository.save(newVoucher);
  }

  public Voucher getById(UUID id) {
    Optional<Voucher> voucher = voucherRepository.findById(id);
    if (voucher.isEmpty()) {
      throw new VoucherNotFoundException(id);
    }
    return voucher.get();
  }

  public List<Voucher> getAll() {
    return voucherRepository.findAll();
  }

  public Voucher deleteById(UUID id) {
    Voucher voucher = authorizedFindVoucherById(id);
    voucherRepository.deleteById(id);
    return voucher;
  }

  public Voucher update(UUID id, VoucherRequest newVoucher) {
    Voucher updatedVoucher = authorizedFindVoucherById(id);
    updatedVoucher.setName(newVoucher.getName());
    updatedVoucher.setDescription(newVoucher.getDescription());
    updatedVoucher.setType(newVoucher.getType());
    updatedVoucher.setStartAt(newVoucher.getStartAt());
    updatedVoucher.setExpiredAt(newVoucher.getExpiredAt());
    return voucherRepository.save(updatedVoucher);
  }

  private boolean isAdmin() {
    return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
  }

  private boolean isAllowedMerchantByVoucher(Voucher voucher) {
    return SecurityContextHolder.getContext().getAuthentication().getName().equals(
      voucher.getMerchant().getUsername()
    );
  }

  private Voucher authorizedFindVoucherById(UUID id) {
    Optional<Voucher> voucher = voucherRepository.findById(id);
    if (voucher.isEmpty()) {
      throw new VoucherNotFoundException(id);
    }
    if (!(isAllowedMerchantByVoucher(voucher.get()) || isAdmin())) {
      throw new AccessDeniedException("You don’t have permission to access this resource");
    }
    return voucher.get();
  }
}

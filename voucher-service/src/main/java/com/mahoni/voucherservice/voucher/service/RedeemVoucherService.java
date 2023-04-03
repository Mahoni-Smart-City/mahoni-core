package com.mahoni.voucherservice.voucher.service;

import com.mahoni.voucherservice.voucher.dto.RedeemVoucherRequest;
import com.mahoni.voucherservice.voucher.dto.RedeemVoucherRequestCRUD;
import com.mahoni.voucherservice.voucher.exception.RedeemVoucherNotFoundException;
import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import com.mahoni.voucherservice.voucher.model.VoucherStatus;
import com.mahoni.voucherservice.voucher.repository.RedeemVoucherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RedeemVoucherService {

  @Autowired
  RedeemVoucherRepository redeemVoucherRepository;

  @Autowired
  VoucherService voucherService;

  public RedeemVoucher create(RedeemVoucherRequestCRUD redeemVoucher) {
    if (!(isAllowedMerchantByVoucherId(redeemVoucher.getVoucherId())|| isAdmin())) {
      throw new AccessDeniedException("You don't have permission to access this resource");
    }
    voucherService.getById(redeemVoucher.getVoucherId()).addQuantity();
    return redeemVoucherRepository.save(new RedeemVoucher(
      voucherService.getById(redeemVoucher.getVoucherId()),
      redeemVoucher.getRedeemCode(),
      redeemVoucher.getExpiredAt()
    ));
  }

  public RedeemVoucher getById(UUID id) {
    Optional<RedeemVoucher> redeemVoucher = redeemVoucherRepository.findById(id);
    if (redeemVoucher.isEmpty()) {
      throw new RedeemVoucherNotFoundException(id);
    }
    return redeemVoucher.get();
  }

  public List<RedeemVoucher> getAll() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (isAdmin()) {
      return redeemVoucherRepository.findAll();
    } else {
      return redeemVoucherRepository.findAllByMerchant(auth.getName());
    }
  }

  public List<RedeemVoucher> getAllByUserId(UUID userId) {
    return redeemVoucherRepository.findAllByUserId(userId);
  }

  public RedeemVoucher update(UUID id, RedeemVoucherRequestCRUD newRedeemVoucher) {
    Optional<RedeemVoucher> redeemVoucher = redeemVoucherRepository.findById(id);
    if (redeemVoucher.isEmpty()) {
      throw new RedeemVoucherNotFoundException(id);
    }
    if (!((isAllowedMerchantByVoucherId(redeemVoucher.get().getVoucher().getId()) &&
      isAllowedMerchantByVoucherId(newRedeemVoucher.getVoucherId()))||
      isAdmin())) {
      throw new AccessDeniedException("You don’t have permission to access this resource");
    }
    RedeemVoucher updatedRedeemVoucher = redeemVoucher.get();
    updatedRedeemVoucher.setVoucher(voucherService.getById(newRedeemVoucher.getVoucherId()));
    updatedRedeemVoucher.setRedeemCode(newRedeemVoucher.getRedeemCode());
    updatedRedeemVoucher.setExpiredAt(newRedeemVoucher.getExpiredAt());
    return redeemVoucherRepository.save(updatedRedeemVoucher);
  }

  public RedeemVoucher deleteById(UUID id) {
    Optional<RedeemVoucher> redeemVoucher = redeemVoucherRepository.findById(id);
    if (redeemVoucher.isEmpty()) {
      throw new RedeemVoucherNotFoundException(id);
    }
    if (!(isAllowedMerchantByVoucherId(redeemVoucher.get().getVoucher().getId())|| isAdmin())) {
      throw new AccessDeniedException("You don't have permission to access this resource");
    }
    redeemVoucherRepository.deleteById(id);
    return redeemVoucher.get();
  }

  public RedeemVoucher redeem(RedeemVoucherRequest request) {

    //TODO: check user point from K-Table

    Optional<RedeemVoucher> redeemVoucher = redeemVoucherRepository.findAvailableRedeemVoucherByVoucherId(request.getVoucherId());
    if (redeemVoucher.isEmpty()) {
      throw new RedeemVoucherNotFoundException(request.getVoucherId());
    }

    //TODO: send event redeemed

    RedeemVoucher updatedRedeemVoucher = redeemVoucher.get();
    updatedRedeemVoucher.setUserId(request.getUserId());
    updatedRedeemVoucher.setStatus(VoucherStatus.ACTIVE);
    updatedRedeemVoucher.setRedeemedAt(LocalDateTime.now());
    updatedRedeemVoucher.getVoucher().subtractQuantity();
    return redeemVoucherRepository.save(updatedRedeemVoucher);
  }

  public RedeemVoucher setRedeemed(UUID id) {
    Optional<RedeemVoucher> redeemVoucher = redeemVoucherRepository.findById(id);
    if (redeemVoucher.isEmpty()) {
      throw new RedeemVoucherNotFoundException(id);
    }
    RedeemVoucher updatedRedeemVoucher = redeemVoucher.get();
    updatedRedeemVoucher.setStatus(VoucherStatus.REDEEMED);
    return redeemVoucherRepository.save(updatedRedeemVoucher);
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void scheduledCheckAndUpdateStatus() {
    List<RedeemVoucher> activeRedeemVoucher = redeemVoucherRepository.findAllByStatus(VoucherStatus.ACTIVE);
    if (!activeRedeemVoucher.isEmpty()) {
      for (RedeemVoucher redeemVoucher: activeRedeemVoucher) {
        LocalDateTime now = LocalDateTime.now();
        if (redeemVoucher.getExpiredAt().isBefore(now)) {
          redeemVoucher.setStatus(VoucherStatus.EXPIRED);
        }
      }
    }
  }

  private boolean isAdmin() {
    return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
  }

  private boolean isAllowedMerchantByVoucherId(UUID id) {
    return SecurityContextHolder.getContext().getAuthentication().getName().equals(
      voucherService.getById(id).getMerchant().getUsername()
    );
  }
}

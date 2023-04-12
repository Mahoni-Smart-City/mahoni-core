package com.mahoni.voucherservice.voucher.service;

import com.mahoni.voucherservice.voucher.dto.RedeemVoucherRequest;
import com.mahoni.voucherservice.voucher.dto.RedeemVoucherRequestCRUD;
import com.mahoni.voucherservice.voucher.exception.RedeemVoucherNotFoundException;
import com.mahoni.voucherservice.voucher.kafka.VoucherEventProducer;
import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import com.mahoni.voucherservice.voucher.model.VoucherStatus;
import com.mahoni.voucherservice.voucher.repository.RedeemVoucherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Autowired
  VoucherEventProducer voucherEventProducer;

  @Value("${spring.voucher.transaction-failed-duration}")
  Integer TRANSACTION_DURATION = 10;

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
    if (!(isAllowedMerchantByVoucherId(redeemVoucher.get().getVoucher().getId()) || isAdmin())) {
      throw new AccessDeniedException("You don’t have permission to access this resource");
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
    updatedRedeemVoucher.getVoucher().subtractQuantity();
    updatedRedeemVoucher.setVoucher(voucherService.getById(newRedeemVoucher.getVoucherId()));
    voucherService.getById(newRedeemVoucher.getVoucherId()).addQuantity();
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
    redeemVoucher.get().getVoucher().subtractQuantity();
    return redeemVoucher.get();
  }

  @Transactional
  public RedeemVoucher redeem(RedeemVoucherRequest request) {

    //TODO: check user point from K-Table

    Optional<RedeemVoucher> redeemVoucher = redeemVoucherRepository.findAvailableRedeemVoucherByVoucherId(request.getVoucherId());
    if (redeemVoucher.isEmpty()) {
      throw new RedeemVoucherNotFoundException(request.getVoucherId());
    }

    //TODO: send event redeemed

    RedeemVoucher updatedRedeemVoucher = redeemVoucher.get();
    updatedRedeemVoucher.setUserId(request.getUserId());
    updatedRedeemVoucher.setStatus(VoucherStatus.PENDING);
    updatedRedeemVoucher.setRedeemedAt(LocalDateTime.now());
    updatedRedeemVoucher.getVoucher().subtractQuantity();
    voucherEventProducer.send(updatedRedeemVoucher);
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
          redeemVoucherRepository.save(redeemVoucher);
        }
      }
    }
  }

  @Scheduled(cron = "*/15 * * * * *")
  public void scheduledCheckAndResetPendingStatus() {
    List<RedeemVoucher> pendingRedeemVoucher = redeemVoucherRepository.findAllByStatus(VoucherStatus.PENDING);
    if (!pendingRedeemVoucher.isEmpty()) {
      for (RedeemVoucher redeemVoucher: pendingRedeemVoucher) {
        LocalDateTime now = LocalDateTime.now();
        if (redeemVoucher.getRedeemedAt().plusMinutes(TRANSACTION_DURATION).isBefore(now)) {
          redeemVoucher.setUserId(null);
          redeemVoucher.setStatus(null);
          redeemVoucher.setRedeemedAt(null);
          redeemVoucher.getVoucher().addQuantity();
          redeemVoucherRepository.save(redeemVoucher);
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

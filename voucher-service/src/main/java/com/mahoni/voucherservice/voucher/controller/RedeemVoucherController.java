package com.mahoni.voucherservice.voucher.controller;

import com.mahoni.schema.UserPointTableSchema;
import com.mahoni.voucherservice.voucher.dto.RedeemVoucherRequest;
import com.mahoni.voucherservice.voucher.dto.RedeemVoucherResponse;
import com.mahoni.voucherservice.voucher.exception.RedeemVoucherNotFoundException;
import com.mahoni.voucherservice.voucher.kafka.VoucherServiceStream;
import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import com.mahoni.voucherservice.voucher.model.VoucherStatus;
import com.mahoni.voucherservice.voucher.service.RedeemVoucherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/redeem")
public class RedeemVoucherController {

  @Autowired
  RedeemVoucherService redeemVoucherService;

  @Autowired
  VoucherServiceStream voucherServiceStream;

  @PostMapping
  public ResponseEntity<RedeemVoucherResponse> post(@Valid @RequestBody RedeemVoucherRequest request) {
    try {
      RedeemVoucher redeemVoucher = redeemVoucherService.redeem(request);
      return ResponseEntity.ok(mapper(redeemVoucher));
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @PostMapping("/{id}")
  public ResponseEntity<RedeemVoucherResponse> setRedeemed(@PathVariable("id") UUID id) {
    try {
      RedeemVoucher redeemVoucher = redeemVoucherService.setRedeemed(id);
      return ResponseEntity.ok(mapper(redeemVoucher));
    } catch (RedeemVoucherNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<List<RedeemVoucherResponse>> getAllByUserId(@PathVariable("id") UUID id) {
      List<RedeemVoucher> allRedeemVouchers = redeemVoucherService.getAllByUserId(id);
      return ResponseEntity.ok(allRedeemVouchers.stream().map(this::mapper).collect(Collectors.toList()));
  }

  private RedeemVoucherResponse mapper(RedeemVoucher redeemVoucher) {
    String code = redeemVoucher.getStatus() == VoucherStatus.PENDING ? "" : redeemVoucher.getRedeemCode();
    return new RedeemVoucherResponse(
      redeemVoucher.getId(),
      redeemVoucher.getVoucher().getId(),
      redeemVoucher.getUserId(),
      code,
      redeemVoucher.getStatus(),
      redeemVoucher.getRedeemedAt(),
      redeemVoucher.getExpiredAt()
    );
  }

  @GetMapping("/point/{id}")
  public ResponseEntity<Object> getPoint(@PathVariable("id") String id) {
      UserPointTableSchema updatedUser = voucherServiceStream.get(id);
      return ResponseEntity.ok(updatedUser.getPoint());
  }
}

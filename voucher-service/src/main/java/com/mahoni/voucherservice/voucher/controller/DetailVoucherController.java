package com.mahoni.voucherservice.voucher.controller;

import com.mahoni.voucherservice.voucher.dto.RedeemVoucherRequestCRUD;
import com.mahoni.voucherservice.voucher.exception.RedeemVoucherNotFoundException;
import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import com.mahoni.voucherservice.voucher.service.RedeemVoucherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vouchers/detail")
public class DetailVoucherController {

  @Autowired
  RedeemVoucherService redeemVoucherService;

  @PostMapping
  public ResponseEntity<RedeemVoucher> post(@Valid @RequestBody RedeemVoucherRequestCRUD requestCRUD) {
    try {
      RedeemVoucher redeemVoucher = redeemVoucherService.create(requestCRUD);
      return ResponseEntity.ok(redeemVoucher);
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping
  private ResponseEntity<List<RedeemVoucher>> getAll() {
    List<RedeemVoucher> allRedeemVouchers = redeemVoucherService.getAll();
    return ResponseEntity.ok(allRedeemVouchers);
  }

  @GetMapping("/{id}")
  public ResponseEntity<RedeemVoucher> get(@PathVariable("id") UUID id) {
    try {
      RedeemVoucher redeemVoucher = redeemVoucherService.getById(id);
      return ResponseEntity.ok(redeemVoucher);
    } catch (RedeemVoucherNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (AccessDeniedException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<RedeemVoucher> update(@PathVariable("id") UUID id, @Valid @RequestBody RedeemVoucherRequestCRUD requestCRUD) {
    try {
      RedeemVoucher updatedRedeemVoucher = redeemVoucherService.update(id, requestCRUD);
      return ResponseEntity.ok(updatedRedeemVoucher);
    } catch (RedeemVoucherNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (AccessDeniedException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<RedeemVoucher> delete(@PathVariable("id") UUID id) {
    try {
      RedeemVoucher deletedRedeemVoucher = redeemVoucherService.deleteById(id);
      return ResponseEntity.ok(deletedRedeemVoucher);
    } catch (RedeemVoucherNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (AccessDeniedException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }
}

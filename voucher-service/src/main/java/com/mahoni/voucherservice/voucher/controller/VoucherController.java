package com.mahoni.voucherservice.voucher.controller;

import com.mahoni.voucherservice.voucher.dto.VoucherRequest;
import com.mahoni.voucherservice.voucher.exception.VoucherNotFoundException;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vouchers")
public class VoucherController {

  @Autowired
  VoucherService voucherService;

  @PostMapping
  public ResponseEntity<Voucher> post(@Valid @RequestBody VoucherRequest request) {
    try {
      Voucher newVoucher = voucherService.create(request);
      return ResponseEntity.ok(newVoucher);
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping
  private ResponseEntity<List<Voucher>> getALl() {
    List<Voucher> allVouchers = voucherService.getAll();
    return ResponseEntity.ok(allVouchers);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Voucher> get(@PathVariable("id") UUID id) {
    try {
      Voucher voucher = voucherService.getById(id);
      return ResponseEntity.ok(voucher);
    } catch (VoucherNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Voucher> delete(@PathVariable("id") UUID id) {
    try {
      Voucher deletedVoucher = voucherService.deleteById(id);
      return ResponseEntity.ok(deletedVoucher);
    } catch (VoucherNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> update(@PathVariable("id") UUID id, @Valid @RequestBody VoucherRequest request) {
    try {
      Voucher updatedVoucher = voucherService.update(id, request);
      return ResponseEntity.ok(updatedVoucher);
    } catch (VoucherNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}

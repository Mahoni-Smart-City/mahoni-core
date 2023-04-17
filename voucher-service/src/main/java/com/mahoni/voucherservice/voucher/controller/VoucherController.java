package com.mahoni.voucherservice.voucher.controller;

import com.mahoni.voucherservice.voucher.dto.VoucherRequest;
import com.mahoni.voucherservice.voucher.dto.VoucherResponse;
import com.mahoni.voucherservice.voucher.exception.VoucherNotFoundException;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/vouchers")
public class VoucherController {

  @Autowired
  VoucherService voucherService;

  @PostMapping
  public ResponseEntity<VoucherResponse> post(@Valid @RequestBody VoucherRequest request) {
    try {
      Voucher newVoucher = voucherService.create(request);
      return ResponseEntity.ok(mapper(newVoucher));
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping
  private ResponseEntity<List<VoucherResponse>> getALl() {
    List<Voucher> allVouchers = voucherService.getAll();
    return ResponseEntity.ok(allVouchers.stream().map(this::mapper).collect(Collectors.toList()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<VoucherResponse> get(@PathVariable("id") UUID id) {
    try {
      Voucher voucher = voucherService.getById(id);
      return ResponseEntity.ok(mapper(voucher));
    } catch (VoucherNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<VoucherResponse> delete(@PathVariable("id") UUID id) {
    try {
      Voucher deletedVoucher = voucherService.deleteById(id);
      return ResponseEntity.ok(mapper(deletedVoucher));
    } catch (VoucherNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (AccessDeniedException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> update(@PathVariable("id") UUID id, @Valid @RequestBody VoucherRequest request) {
    try {
      Voucher updatedVoucher = voucherService.update(id, request);
      return ResponseEntity.ok(mapper(updatedVoucher));
    } catch (VoucherNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (AccessDeniedException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  private VoucherResponse mapper(Voucher voucher) {
    return new VoucherResponse(
      voucher.getId(),
      voucher.getName(),
      voucher.getDescription(),
      voucher.getType(),
      voucher.getPoint(),
      voucher.getStartAt(),
      voucher.getExpiredAt(),
      voucher.getMerchant().getId(),
      voucher.getQuantity()
    );
  }
}

package com.mahoni.voucherservice.merchant.controller;

import com.mahoni.voucherservice.merchant.dto.MerchantRequest;
import com.mahoni.voucherservice.merchant.exception.MerchantNotFoundException;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.service.MerchantService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchants")
@Slf4j
public class MerchantController {

  @Autowired
  MerchantService merchantService;

  @PostMapping
  public ResponseEntity<Merchant> post(@Valid @RequestBody MerchantRequest request) {
    try {
      Merchant newMerchant = merchantService.create(request);
      return ResponseEntity.ok(newMerchant);
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping
  private ResponseEntity<List<Merchant>> getAll() {
    List<Merchant> allMerchants = merchantService.getAll();
    return ResponseEntity.ok(allMerchants);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Merchant> get(@PathVariable("id") UUID id ) {
    try {
      Merchant merchant = merchantService.getById(id);
      return ResponseEntity.ok(merchant);
    } catch (MerchantNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Merchant> delete(@PathVariable("id") UUID id) {
    try {
      Merchant deletedMerchant = merchantService.deleteById(id);
      return ResponseEntity.ok(deletedMerchant);
    } catch (MerchantNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> update(@PathVariable("id") UUID id, @Valid @RequestBody MerchantRequest request) {
    try {
      Merchant updatedMerchant = merchantService.update(id, request);
      return ResponseEntity.ok(updatedMerchant);
    } catch (MerchantNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
package com.mahoni.voucherservice.merchant.controller;

import com.mahoni.voucherservice.merchant.dto.MerchantRequest;
import com.mahoni.voucherservice.merchant.dto.MerchantResponse;
import com.mahoni.voucherservice.merchant.exception.MerchantNotFoundException;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.service.MerchantService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1/merchants")
@Slf4j
public class MerchantController {

  @Autowired
  MerchantService merchantService;

  @PostMapping
  public ResponseEntity<MerchantResponse> post(@Valid @RequestBody MerchantRequest request) {
    try {
      Merchant newMerchant = merchantService.create(request);
      return ResponseEntity.ok(mapper(newMerchant));
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping
  private ResponseEntity<List<MerchantResponse>> getAll() {
    List<Merchant> allMerchants = merchantService.getAll();
    return ResponseEntity.ok(allMerchants.stream().map(this::mapper).collect(Collectors.toList()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<MerchantResponse> get(@PathVariable("id") UUID id ) {
    try {
      Merchant merchant = merchantService.getById(id);
      return ResponseEntity.ok(mapper(merchant));
    } catch (MerchantNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MerchantResponse> delete(@PathVariable("id") UUID id) {
    try {
      Merchant deletedMerchant = merchantService.deleteById(id);
      return ResponseEntity.ok(mapper(deletedMerchant));
    } catch (MerchantNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (AccessDeniedException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> update(@PathVariable("id") UUID id, @Valid @RequestBody MerchantRequest request) {
    try {
      Merchant updatedMerchant = merchantService.update(id, request);
      return ResponseEntity.ok(mapper(updatedMerchant));
    } catch (MerchantNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (AccessDeniedException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  private MerchantResponse mapper(Merchant merchant) {
    return new MerchantResponse(merchant.getId(), merchant.getUsername(), merchant.getName(), merchant.getEmail());
  }
}

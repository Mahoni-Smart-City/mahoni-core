package com.mahoni.voucherservice.merchant.exception;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public class MerchantNotFoundException extends RuntimeException {
  public MerchantNotFoundException(UUID id) {
    super("Merchant with id " + id + " is not found");
  }

  public MerchantNotFoundException(String username) {
    super("Merchant with username " + username + " is not found");
  }
}

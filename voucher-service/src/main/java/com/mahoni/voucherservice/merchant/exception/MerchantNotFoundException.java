package com.mahoni.voucherservice.merchant.exception;

import java.util.UUID;

public class MerchantNotFoundException extends RuntimeException {
  public MerchantNotFoundException(UUID id) {
    super("Merchant with id " + id + " is not found");
  }

  public MerchantNotFoundException(String username) {
    super("Merchant with username " + username + " is not found");
  }
}

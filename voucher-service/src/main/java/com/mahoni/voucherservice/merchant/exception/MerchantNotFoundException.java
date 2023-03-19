package com.mahoni.voucherservice.merchant.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MerchantNotFoundException extends RuntimeException {
  public MerchantNotFoundException(Long id) {
    super("Merchant with id " + id + " is not found");
  }
}

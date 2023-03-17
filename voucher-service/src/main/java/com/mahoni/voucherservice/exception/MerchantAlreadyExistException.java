package com.mahoni.voucherservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MerchantAlreadyExistException extends RuntimeException {
  public MerchantAlreadyExistException(String username) {
    super("Merchant with username " + username + " is already exists");
  }
}

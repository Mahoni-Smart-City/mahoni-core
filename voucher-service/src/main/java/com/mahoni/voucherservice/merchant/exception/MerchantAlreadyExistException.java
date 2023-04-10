package com.mahoni.voucherservice.merchant.exception;

public class MerchantAlreadyExistException extends RuntimeException {
  public MerchantAlreadyExistException(String username) {
    super("Merchant with username " + username + " is already exists");
  }
}

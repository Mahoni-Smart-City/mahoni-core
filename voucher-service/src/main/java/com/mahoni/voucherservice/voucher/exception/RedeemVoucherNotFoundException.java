package com.mahoni.voucherservice.voucher.exception;

import java.util.UUID;

public class RedeemVoucherNotFoundException extends RuntimeException {
  public RedeemVoucherNotFoundException(UUID id) {
    super("Voucher redeemed with id " + id + " is not found");
  }
}

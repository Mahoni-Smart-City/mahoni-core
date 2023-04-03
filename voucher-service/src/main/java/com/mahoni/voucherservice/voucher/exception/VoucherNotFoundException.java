package com.mahoni.voucherservice.voucher.exception;

import java.util.UUID;

public class VoucherNotFoundException extends RuntimeException {
  public VoucherNotFoundException(UUID id) {
    super("Voucher with id " + id + " is not found");
  }
}

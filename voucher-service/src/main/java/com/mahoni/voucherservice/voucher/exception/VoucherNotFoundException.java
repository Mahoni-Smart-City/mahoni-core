package com.mahoni.voucherservice.voucher.exception;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public class VoucherNotFoundException extends RuntimeException {
  public VoucherNotFoundException(UUID id) {
    super("Voucher with id " + id + " is not found");
  }
}

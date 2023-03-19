package com.mahoni.voucherservice.voucher.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class VoucherNotFoundException extends RuntimeException {
  public VoucherNotFoundException(Long id) {
    super("Voucher with id " + id + " is not found");
  }
}

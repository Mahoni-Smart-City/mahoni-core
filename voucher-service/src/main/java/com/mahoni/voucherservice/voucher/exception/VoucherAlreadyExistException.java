package com.mahoni.voucherservice.voucher.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class VoucherAlreadyExistException extends RuntimeException {
  public VoucherAlreadyExistException(String name, String code) {
    super("Voucher with name " + name + " and code " + code + " is already exists");
  }
}

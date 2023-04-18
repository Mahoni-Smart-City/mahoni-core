package com.mahoni.voucherservice.voucher.exception;

import java.util.UUID;

public class InsufficientUserPointException extends RuntimeException{
  public InsufficientUserPointException() {
    super("Insufficient point");
  }
}

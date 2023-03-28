package com.mahoni.tripservice.qrgenerator.exception;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public class QRGeneratorNotFoundException extends RuntimeException {
  public QRGeneratorNotFoundException(UUID id) {
    super("QR Generator with id " + id + " is not found");
  }
}

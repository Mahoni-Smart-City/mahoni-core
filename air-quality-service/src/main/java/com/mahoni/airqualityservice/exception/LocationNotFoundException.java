package com.mahoni.airqualityservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LocationNotFoundException extends RuntimeException {
  public LocationNotFoundException(Long id) {
    super("Location with id " + id + " is not found");
  }
}

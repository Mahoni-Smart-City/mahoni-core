package com.mahoni.userservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceAlreadyExistException extends RuntimeException {
  public ResourceAlreadyExistException(String message) {
    super(message);
  }
}

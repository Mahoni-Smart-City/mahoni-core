package com.mahoni.userservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(Long id) {
    super("User with id " + id + " is not found");
  }
}

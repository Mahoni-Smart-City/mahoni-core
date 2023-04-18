package com.mahoni.userservice.exception;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(UUID id) {
    super("User with id " + id + " is not found");
  }
}

package com.mahoni.userservice.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(UUID id) {
    super("User with id " + id + " is not found");
  }
}

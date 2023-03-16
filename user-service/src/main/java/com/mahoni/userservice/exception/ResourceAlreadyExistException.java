package com.mahoni.userservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceAlreadyExistException extends RuntimeException {
  public ResourceAlreadyExistException(String username) {
    super("User with username " + username + " is already exists");
  }
}

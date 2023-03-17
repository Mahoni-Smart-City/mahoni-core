package com.mahoni.userservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
  @NotNull
  private String username;
  @NotNull
  private String name;
  @NotNull
  private String email;
}

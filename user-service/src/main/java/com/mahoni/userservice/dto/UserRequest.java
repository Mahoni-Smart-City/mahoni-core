package com.mahoni.userservice.dto;

import com.mahoni.userservice.model.Sex;
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

  @NotNull
  private Sex sex;

  @NotNull
  private Integer yearOfBirth;
}

package com.mahoni.voucherservice.merchant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantResponse {
  @NotNull
  private UUID id;
  @NotNull
  private String username;
  @NotNull
  private String name;
  @NotNull
  private String email;
}

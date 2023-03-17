package com.mahoni.voucherservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherRequest {
  @NotNull
  private String name;
  @NotNull
  private String description;
  @NotNull
  private String code;
  @NotNull
  private LocalDateTime start_at;
  @NotNull
  private LocalDateTime expired_at;

}

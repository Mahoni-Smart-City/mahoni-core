package com.mahoni.voucherservice.voucher.dto;

import com.mahoni.voucherservice.voucher.model.VoucherType;
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
  private VoucherType type;
  @NotNull
  private Integer point;
  @NotNull
  private LocalDateTime startAt;
  @NotNull
  private LocalDateTime expiredAt;
}

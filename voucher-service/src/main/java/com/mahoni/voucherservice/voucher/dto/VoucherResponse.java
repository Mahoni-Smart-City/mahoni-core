package com.mahoni.voucherservice.voucher.dto;

import com.mahoni.voucherservice.voucher.model.VoucherType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherResponse {
  @NotNull
  private UUID id;
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
  @NotNull
  private UUID merchantId;
  @NotNull
  private Integer quantity;
}

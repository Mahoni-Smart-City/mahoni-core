package com.mahoni.voucherservice.voucher.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedeemVoucherRequest {
  @NotNull
  private UUID voucherId;
  @NotNull
  private UUID userId;
}

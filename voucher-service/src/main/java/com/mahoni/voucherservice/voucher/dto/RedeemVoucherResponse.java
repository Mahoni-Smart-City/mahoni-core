package com.mahoni.voucherservice.voucher.dto;

import com.mahoni.voucherservice.voucher.model.VoucherStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedeemVoucherResponse {
  @NotNull
  private UUID id;
  @NotNull
  private UUID voucherId;
  @NotNull
  private UUID userId;
  @NotNull
  private String redeemCode;
  @NotNull
  private VoucherStatus status;
  @NotNull
  private LocalDateTime redeemedAt;
  @NotNull
  private LocalDateTime expiredAt;
}

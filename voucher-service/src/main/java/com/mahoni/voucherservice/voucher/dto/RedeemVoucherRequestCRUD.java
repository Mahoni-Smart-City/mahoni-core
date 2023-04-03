package com.mahoni.voucherservice.voucher.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedeemVoucherRequestCRUD {
  @NotNull
  private UUID voucherId;
  @NotNull
  private String redeemCode;
  @NotNull
  private LocalDateTime expiredAt;
}

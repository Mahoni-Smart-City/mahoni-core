package com.mahoni.voucherservice.voucher.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "redeem_vouchers")
public class RedeemVoucher {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
    name = "UUID",
    strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "voucher_id")
  private Voucher voucher;

  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "redeem_code")
  private String redeemCode;

  @Enumerated(EnumType.ORDINAL)
  private VoucherStatus status;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "redeemed_at")
  private LocalDateTime redeemedAt;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "expired_at")
  private LocalDateTime expiredAt;

  public RedeemVoucher(Voucher voucher, String redeemCode, LocalDateTime expiredAt) {
    this.voucher = voucher;
    this.redeemCode = redeemCode;
    this.expiredAt = expiredAt;
  }
}

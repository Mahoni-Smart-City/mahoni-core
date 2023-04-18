package com.mahoni.voucherservice.voucher.model;

import com.mahoni.voucherservice.merchant.model.Merchant;
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
@Table(name = "vouchers")
public class Voucher {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
    name = "UUID",
    strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Enumerated(EnumType.ORDINAL)
  private VoucherType type;

  @Column(nullable = false)
  private Integer point;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "start_at")
  private LocalDateTime startAt;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "expired_at")
  private LocalDateTime expiredAt;

  @ManyToOne
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;

  private Integer quantity;

  public Voucher(String name, String description, VoucherType type, Integer point, LocalDateTime startAt, LocalDateTime expiredAt, Merchant merchant) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.point = point;
    this.startAt = startAt;
    this.expiredAt = expiredAt;
    this.merchant = merchant;
  }

  public void addQuantity() {
    this.quantity = this.quantity + 1;
  }

  public void subtractQuantity() {
    this.quantity = this.quantity - 1;
  }
}

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
@Table(name = "vouchers")
public class Voucher {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
    name = "UUID",
    strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id", updatable = false, nullable = false)
  @Getter
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String code;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "start_at")
  private LocalDateTime startAt;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "expired_at")
  private LocalDateTime expiredAt;

  public Voucher(String name, String description, String code, LocalDateTime startAt, LocalDateTime expiredAt) {
    this.name = name;
    this.description = description;
    this.code = code;
    this.startAt = startAt;
    this.expiredAt = expiredAt;
  }
}

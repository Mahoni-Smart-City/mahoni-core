package com.mahoni.voucherservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "vouchers")
public class Voucher {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String code;

  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime start_at;

  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime expired_at;

  public Voucher(String name, String description, String code, LocalDateTime start_at, LocalDateTime expired_at) {
    this.name = name;
    this.description = description;
    this.code = code;
    this.start_at = start_at;
    this.expired_at = expired_at;
  }
}
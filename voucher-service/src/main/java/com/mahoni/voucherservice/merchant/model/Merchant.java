package com.mahoni.voucherservice.merchant.model;

import com.mahoni.voucherservice.voucher.model.Voucher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "merchants")
public class Merchant {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
    name = "UUID",
    strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id", updatable = false, nullable = false)
  @Getter
  private UUID id;

  @Column(unique = true)
  private String username;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String email;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "merchant_id", referencedColumnName = "id")
  private List<Voucher> vouchers;

  public Merchant(String username, String name, String email) {
    this.username = username;
    this.name = name;
    this.email = email;
  }
}

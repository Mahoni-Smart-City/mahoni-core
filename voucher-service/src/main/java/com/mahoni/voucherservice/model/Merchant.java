package com.mahoni.voucherservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "merchants")
public class Merchant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private Long id;

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
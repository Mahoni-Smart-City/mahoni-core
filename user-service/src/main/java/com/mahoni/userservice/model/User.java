package com.mahoni.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

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

  @Column
  private Integer point;

  public User(String username, String name, String email, Integer point) {
    this.username = username;
    this.name = name;
    this.email = email;
    this.point = point;
  }
}

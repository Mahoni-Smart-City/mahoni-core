package com.mahoni.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator( name = "UUID", strategy = "org.hibernate.id.UUIDGenerator" )
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(unique = true)
  private String username;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String email;

  @Column
  @Enumerated(EnumType.ORDINAL)
  private Sex sex;

  @Column(name = "yob")
  private Integer yearOfBirth;

  @Column
  private Integer point;

  public User(String username, String name, String email, Sex sex, Integer yearOfBirth, Integer point) {
    this.username = username;
    this.name = name;
    this.email = email;
    this.sex = sex;
    this.yearOfBirth = yearOfBirth;
    this.point = point;
  }

  public Boolean sufficientPoint(Integer point) {
    return this.point - point >= 0;
  }

  public void subtractPoint(Integer point) {
    if (sufficientPoint(point)) {
      this.point = this.point - point;
    }
  }

  public void addPoint(Integer point) {
    this.point = this.point + point;
  }
}

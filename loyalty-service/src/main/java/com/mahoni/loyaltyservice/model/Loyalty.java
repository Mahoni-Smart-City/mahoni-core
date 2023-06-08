package com.mahoni.loyaltyservice.model;

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
@Table(name = "loyalties")
public class Loyalty {

  @Id
  private UUID userId;

  @Column(name = "loyalty_point")
  private Integer loyaltyPoint;
}

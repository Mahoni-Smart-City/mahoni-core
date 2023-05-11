package com.mahoni.airqualityservice.model;

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
@Table(name = "locations")
public class Location {
  @Id
  private Long id;
  @Column
  private String district;
  @Column(name = "sub_district")
  private String subDistrict;
  @Column
  private String village;
  @Column
  private Double longitude;
  @Column
  private Double latitude;
}

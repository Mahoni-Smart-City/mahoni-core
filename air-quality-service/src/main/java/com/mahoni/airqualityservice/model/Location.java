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
  private String city;

  @Column(name = "subdistrict")
  private String subDistrict;

  @Column
  private String latitude;

  @Column
  private String longitude;
}

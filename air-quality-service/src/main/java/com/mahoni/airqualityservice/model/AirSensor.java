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
@Table(name = "air_sensors")
public class AirSensor {
  @Id
  private Long id;

  @Column(name = "location_name")
  private String locationName;

  @ManyToOne
  @JoinColumn(name = "location_id")
  private Location location;
}

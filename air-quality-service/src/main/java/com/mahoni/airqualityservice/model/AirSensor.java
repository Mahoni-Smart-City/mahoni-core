package com.mahoni.airqualityservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "air_sensors")
public class AirSensor {
  @Id
  private Long id;

  @Column(name = "name_location")
  private String nameLocation;

  @ManyToOne
  @JoinColumn(name = "id_location")
  private Location location;
}

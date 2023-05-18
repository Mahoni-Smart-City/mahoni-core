package com.mahoni.tripservice.qrgenerator.model;

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
@Table(name = "qr_generators")
public class QRGenerator {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator( name = "UUID", strategy = "org.hibernate.id.UUIDGenerator" )
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column
  private String location;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private QRGeneratorType type;

  @Column(name = "sensor_id_1", nullable = false)
  private Long sensorId1;

  @Column(name = "sensor_id_2")
  private Long sensorId2;

  public QRGenerator(String location, QRGeneratorType type, Long sensorId1, Long sensorId2) {
    this.location = location;
    this.type = type;
    this.sensorId1 = sensorId1;
    this.sensorId2 = sensorId2;
  }

  public QRGenerator(String location, QRGeneratorType type, Long sensorId1) {
    this.location = location;
    this.type = type;
    this.sensorId1 = sensorId1;
  }
}

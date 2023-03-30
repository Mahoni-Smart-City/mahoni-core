package com.mahoni.tripservice.trip.model;

import com.mahoni.tripservice.qrgenerator.model.QRGenerator;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "trips")
public class Trip {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator( name = "UUID", strategy = "org.hibernate.id.UUIDGenerator" )
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private UUID userId;

  @ManyToOne
  @JoinColumn(name = "scan_in_place_id", referencedColumnName = "id", nullable = false)
  private QRGenerator scanInPlaceId;

  @ManyToOne
  @JoinColumn(name = "scan_out_place_id", referencedColumnName = "id")
  private QRGenerator scanOutPlaceId;

  @Column(name = "scan_in_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime scanInAt;

  @Column(name = "scan_out_at")
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime scanOutAt;

  @Column
  private String status;

  @Column
  private Double aqi;

  @Column
  private Integer point;

  public Trip(UUID userId, QRGenerator scanInPlaceId, LocalDateTime scanInAt, String status) {
    this.userId = userId;
    this.scanInPlaceId = scanInPlaceId;
    this.scanInAt = scanInAt;
    this.status = status;
  }
}

package com.mahoni.airqualityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirQualityResponse {
  private Long eventId;
  private String sensorId;
  private Long timestamp;
  private double aqi;
  private double no2;
  private double pm25;
  private double pm10;
}

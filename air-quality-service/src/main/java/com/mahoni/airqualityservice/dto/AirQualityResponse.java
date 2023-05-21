package com.mahoni.airqualityservice.dto;

import com.mahoni.airqualityservice.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirQualityResponse {
  private Long sensorId;
  private Long timestamp;
  private Double aqi;
  private String category;
  private Double co;
  private Double no;
  private Double no2;
  private Double o3;
  private Double so2;
  private Double pm25;
  private Double pm10;
  private Double nh3;
  private Double pressure;
  private Double humidity;
  private Double temperature;
  private Location location;
  private List<String> recommendation;
}

package com.mahoni.airqualityservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirSensorRequest {
  @NotNull
  private Long id;
  private String nameLocation;
  @NotNull
  private Long idLocation;
}

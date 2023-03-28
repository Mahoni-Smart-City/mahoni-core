package com.mahoni.tripservice.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QRGeneratorRequest {
  private String location;
  @NotNull
  private  QRGeneratorType type;
  @NotNull
  private UUID sensorId1;
  private UUID sensorId2;
}

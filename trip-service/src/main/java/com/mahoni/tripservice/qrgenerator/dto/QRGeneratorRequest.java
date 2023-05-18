package com.mahoni.tripservice.qrgenerator.dto;

import com.mahoni.tripservice.qrgenerator.model.QRGeneratorType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QRGeneratorRequest {
  private String location;
  @NotNull
  private QRGeneratorType type;
  @NotNull
  private String sensorId1;
  private String sensorId2;
}

package com.mahoni.tripservice.trip.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripRequest {
  @NotNull
  private String qrToken;
  @NotNull
  private UUID userId;
  @NotNull
  private UUID scanPlaceId;
}

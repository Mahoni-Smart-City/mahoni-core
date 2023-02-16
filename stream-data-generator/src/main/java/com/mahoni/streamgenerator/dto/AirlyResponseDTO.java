package com.mahoni.streamgenerator.dto;

import lombok.Data;

import java.util.List;

@Data
public class AirlyResponseDTO {

    AirlyMeasurement current;
    List<AirlyMeasurement> history;
    List<AirlyMeasurement> forecast;
}

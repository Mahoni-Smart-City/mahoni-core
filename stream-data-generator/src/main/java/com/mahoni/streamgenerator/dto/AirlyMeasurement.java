package com.mahoni.streamgenerator.dto;

import lombok.Data;

import java.util.List;

@Data
public class AirlyMeasurement {
    String fromDateTime;
    String tillDateTime;
    List<AirlyValue> values;
    List<AirlyIndex> indexes;
    List<AirlyStandard> standards;
}

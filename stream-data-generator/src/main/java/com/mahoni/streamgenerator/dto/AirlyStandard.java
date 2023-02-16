package com.mahoni.streamgenerator.dto;

import lombok.Data;

@Data
public class AirlyStandard {
    String name;
    String pollutant;
    Double limit;
    Double percent;
    String averaging;
}

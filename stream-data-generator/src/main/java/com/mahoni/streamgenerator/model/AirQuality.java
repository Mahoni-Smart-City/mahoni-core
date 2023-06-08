package com.mahoni.streamgenerator.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "AIR_QUALITY_RAW")
//@AllArgsConstructor
@NoArgsConstructor
public class AirQuality {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;

    @Id
    private String sensorId;

    @Column
    private Long timestamp;

    @Column
    private Double aqi;

    @Column
    private Double co;

    @Column
    private Double no;

    @Column
    private Double no2;

    @Column
    private Double o3;

    @Column
    private Double so2;

    @Column
    private Double pm25;

    @Column
    private Double pm10;

    @Column
    private Double pm1;

    @Column
    private Double nh3;

    @Column
    private Double pressure;

    @Column
    private Double humidity;

    @Column
    private Double temperature;


    public AirQuality(String sensorId, Long timestamp, Double aqi, Double co, Double no, Double no2, Double o3, Double so2, Double pm25, Double pm10, Double pm1, Double nh3, Double pressure, Double humidity, Double temperature) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.aqi = aqi;
        this.co = co;
        this.no = no;
        this.no2 = no2;
        this.o3 = o3;
        this.so2 = so2;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.pm1 = pm1;
        this.nh3 = nh3;
        this.pressure = pressure;
        this.humidity = humidity;
        this.temperature = temperature;
    }
}

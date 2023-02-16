package com.mahoni.streamgenerator.controller;

import com.mahoni.streamgenerator.dto.AirlyResponseDTO;
import com.mahoni.streamgenerator.model.AirQuality;
import com.mahoni.streamgenerator.repository.AirQualityStreamRepository;
import com.mahoni.streamgenerator.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/streams")
public class StreamController {

    @Autowired
    AirQualityStreamRepository airQualityStreamRepository;

    @Autowired
    SchedulerService schedulerService;

    @GetMapping
    public List<AirQuality> getAll() {
        return airQualityStreamRepository.findAll();
    }

    @PostMapping
    public AirQuality post() {
        return airQualityStreamRepository.save(new AirQuality("test",1676012906L,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0, 1.0, 1.0, 1.0));
    }

    @PostMapping("/fetch-save")
    public void fetchAndSave(){
        Map<String, AirlyResponseDTO> data = schedulerService.getAirlyData();
        schedulerService.parseAndSaveData(data);
    }

    @GetMapping("/airly")
    public Map<String, AirlyResponseDTO> getAirly() {
        return schedulerService.getAirlyData();
    }
}

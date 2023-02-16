package com.mahoni.streamgenerator.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.schema.AirQualityRawSchema;
import com.mahoni.streamgenerator.dto.AirlyMeasurement;
import com.mahoni.streamgenerator.dto.AirlyResponseDTO;
import com.mahoni.streamgenerator.model.AirQuality;
import com.mahoni.streamgenerator.repository.AirQualityStreamRepository;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class SchedulerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final List<String> airlyLocationIds;

    private final String airlyApiKey1;

    private final String AIRLY_BASE_URL = "https://airapi.airly.eu/v2";

    @Value("${RANDOMIZE_STREAM_DATA}")
    private Boolean randomizeStreamData = false;

    @Value("${ENABLE_STREAM}")
    private Boolean enableStream = false;

    private String TOPIC = "air-quality-raw-topic";

    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String, AirQualityRawSchema> kafkaTemplate;

    OkHttpClient okhttp = new OkHttpClient();

    Random rand = new Random();

    @Autowired
    private AirQualityStreamRepository airQualityStreamRepository;

    public SchedulerService(@Value("${api.airly.locationId}") List<String> airlyLocationIds, @Value("${api.airly.key1}") String airlyApiKey1) {
        this.airlyLocationIds = airlyLocationIds;
        this.airlyApiKey1 = airlyApiKey1;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

    }

    public Map<String, AirlyResponseDTO> getAirlyData() {
        Map<String, AirlyResponseDTO> result = new HashMap<>();
        for (String locationId: airlyLocationIds) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(AIRLY_BASE_URL + "/measurements/installation")
                    .newBuilder()
                    .addQueryParameter("installationId", locationId);

            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .header("apiKey", airlyApiKey1)
                    .build();
            try {
                ResponseBody response = okhttp.newCall(request).execute().body();
                AirlyResponseDTO mappedObject = objectMapper.readValue(response.string(), AirlyResponseDTO.class);
                result.put(locationId, mappedObject);
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
        return result;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledAirly() {
        logger.info("Running scheduled airly");
        Map<String, AirlyResponseDTO> data = getAirlyData();
        parseAndSaveData(data);
    }

    @Scheduled(cron = "*/5 * * * * *")
    public void sendToKafka() {
        logger.info("Running send to kafka");
        if (enableStream) {
            LocalDateTime datetime = LocalDateTime.now();
            LocalDateTime rounded = datetime.minusMinutes(datetime.getMinute()).minusSeconds(datetime.getSecond());
            Long timestamp = rounded.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(rounded)) * 1000L;

            for (String sensorId: airlyLocationIds) {
            logger.info(timestamp.toString());
                AirQuality airQuality = airQualityStreamRepository.findByTimestampAndSensorId(timestamp, sensorId);
                if (randomizeStreamData || airQuality == null){
                    airQuality = new AirQuality(
                        rand.nextLong(),
                        sensorId,
                        timestamp,
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble()
                    );
                }
                ProducerRecord<String, AirQualityRawSchema> record = createRecord(airQuality);
                logger.info(record.toString());
                kafkaTemplate.send(record);
            }
        }
    }

    public void parseAndSaveData(Map<String, AirlyResponseDTO> data) {
        for (Map.Entry<String, AirlyResponseDTO> set: data.entrySet()) {
            List<AirlyMeasurement> history = set.getValue().getHistory();
            for (AirlyMeasurement measurement: history) {
                Long timestamp = Instant.parse(measurement.getTillDateTime()).toEpochMilli();

                Map<String, Double> flatten = new HashMap<>();
                measurement.getValues().forEach(value ->
                    flatten.put(value.getName(), value.getValue())
                );
                AirQuality airQuality = new AirQuality(
                    set.getKey(),
                    Instant.parse(measurement.getTillDateTime()).toEpochMilli(),
                    measurement.getIndexes().get(0).getValue(),
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    flatten.get("PM25"),
                    flatten.get("PM10"),
                    flatten.get("PM1"),
                    0.0,
                    flatten.get("PRESSURE"),
                    flatten.get("HUMIDITY"),
                    flatten.get("TEMPERATURE")
                );
                logger.info(airQuality.toString());
                airQualityStreamRepository.save(airQuality);
            }
        }
    }

    private ProducerRecord<String, AirQualityRawSchema> createRecord(AirQuality data) {

        String id = UUID.randomUUID().toString();

        AirQualityRawSchema event = AirQualityRawSchema.newBuilder()
            .setEventId(data.getId().toString())
            .setSensorId(data.getSensorId())
            .setTimestamp(data.getTimestamp())
            .setAqi(data.getAqi())
            .setCo(data.getCo())
            .setNo(data.getNo())
            .setNo2(data.getNo2())
            .setO3(data.getO3())
            .setSo2(data.getSo2())
            .setPm25(data.getPm25())
            .setPm10(data.getPm10())
            .setPm1(data.getPm1())
            .setNh3(data.getNh3())
            .setPressure(data.getPressure())
            .setHumidity(data.getHumidity())
            .build();

        return new ProducerRecord<>(TOPIC, id, event);
    }
}

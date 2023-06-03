package com.mahoni.airqualityservice.kafka;

import com.mahoni.flink.schema.AirQualityProcessedSchema;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Component
public class AirQualityServiceStream {

  @Autowired
  StreamsBuilderFactoryBean factoryBean;

  @Autowired
  KafkaTemplate<String, AirQualityProcessedSchema> kafkaTemplate;

  @Autowired
  public void compactedTopic(KStream<String, AirQualityProcessedSchema> kStream) {
    KStream<String, AirQualityProcessedSchema> airQualityCompacted = kStream
      .map((s, airQualityProcessedSchema) -> KeyValue.pair(AirQualityServiceStream.parseTableKey(airQualityProcessedSchema), airQualityProcessedSchema));
    airQualityCompacted.to(KafkaTopic.AIR_QUALITY_COMPACTED);
    airQualityCompacted.toTable(Materialized.as("air-quality-state-store"));
  }

//  @Autowired
//  public KTable<String, AirQualityProcessedSchema> kTable(KStream<String, AirQualityProcessedSchema> kStream) {
//    return kStream.toTable(Materialized.as("air-quality-state-store"));
//  }
//    KGroupedStream<String, AirQualityProcessedSchema> groupedAirQuality = kStream
//      .map((s, airQualityProcessedSchema) -> KeyValue.pair(AirQualityServiceStream.parseTableKey(airQualityProcessedSchema), airQualityProcessedSchema))
//      .groupByKey();
//
//    KTable<String, AirQualityProcessedSchema> airQualityTable = groupedAirQuality
//      .reduce((airQualityProcessedSchema, v1) -> v1, Materialized.as("air-quality-state-store"));
//
//    airQualityTable.toStream().to(KafkaTopic.AIR_QUALITY_COMPACTED);

//    return airQualityTable;
//  }

  public AirQualityProcessedSchema get(String id) {
    KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
    assert kafkaStreams != null;
    ReadOnlyKeyValueStore<String, AirQualityProcessedSchema> amounts = kafkaStreams
      .store(StoreQueryParameters.fromNameAndType("air-quality-state-store", QueryableStoreTypes.keyValueStore()));
    return amounts.get(id);
  }

  public static String  parseTableKey(AirQualityProcessedSchema airQualityProcessedSchema) {
    LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(airQualityProcessedSchema.getTimestamp()), TimeZone.getDefault().toZoneId());
    return timestamp.getDayOfWeek().name() + ":" + timestamp.getHour() + ":" + airQualityProcessedSchema.getSensorId();
  }

  public static String  parseTableKey(Long sensorId) {
    LocalDateTime datetime = LocalDateTime.now();
    return datetime.getDayOfWeek().name() + ":" + datetime.getHour() + ":" + sensorId;
  }

  public static String  parseTableKey(AirQualityProcessedSchema airQualityProcessedSchema, LocalDateTime timestamp) {
    return timestamp.getDayOfWeek().name() + ":" + timestamp.getHour() + ":" + airQualityProcessedSchema.getSensorId();
  }
}

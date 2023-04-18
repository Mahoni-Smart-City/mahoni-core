package com.mahoni.airqualityservice.kafka;

import com.mahoni.schema.AirQualityProcessedSchema;
import com.mahoni.schema.AirQualityTableSchema;
import com.mahoni.schema.UserPointSchema;
import com.mahoni.schema.UserPointTableSchema;
import org.apache.kafka.clients.producer.ProducerRecord;
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

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
public class AirQualityServiceStream {

  @Autowired
  StreamsBuilderFactoryBean factoryBean;

  @Autowired
  KafkaTemplate<String, AirQualityProcessedSchema> kafkaTemplate;

  @Autowired
  public KTable<String, AirQualityTableSchema> kTable(KStream<String, AirQualityProcessedSchema> kStream) {
    KGroupedStream<String, AirQualityProcessedSchema> groupedAirQuality = kStream
      .map((s, airQualityProcessedSchema) -> {
        String key = airQualityProcessedSchema.getTimestamp() + ":" + airQualityProcessedSchema.getSensorId();
        return KeyValue.pair(key, airQualityProcessedSchema);
      })
      .groupByKey();

    KTable<String, AirQualityTableSchema> airQualityTable = groupedAirQuality
      .reduce((airQualityProcessedSchema, v1) -> v1)
      .mapValues((s, airQualityProcessedSchema) ->   AirQualityTableSchema.newBuilder()
        .setSensorId(airQualityProcessedSchema.getSensorId())
        .setAqi(airQualityProcessedSchema.getAqi())
        .build());

    airQualityTable.toStream().to(KafkaTopic.AIR_QUALITY_COMPACTED);

    return airQualityTable;
  }

  public AirQualityTableSchema get(String id) {
    KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
    assert kafkaStreams != null;
    ReadOnlyKeyValueStore<String, AirQualityTableSchema> amounts = kafkaStreams
      .store(StoreQueryParameters.fromNameAndType("user-point-state-store", QueryableStoreTypes.keyValueStore()));
    return amounts.get(id);
  }
}

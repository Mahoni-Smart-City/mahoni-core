package com.mahoni.tripservice.trip.kafka;

import com.mahoni.schema.AirQualityProcessedSchema;
import com.mahoni.schema.AirQualityTableSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TripServiceStream {
  @Autowired
  StreamsBuilderFactoryBean factoryBean;

  @Autowired
  public KTable<String, AirQualityProcessedSchema> airQualityKtable(KStream<String, AirQualityProcessedSchema> kStream) {
    return kStream.toTable(Materialized.as("air-quality-state-store"));
  }

  public AirQualityProcessedSchema getAirQuality(String id) {
    log.info("GET AIR QUALITY" + id);
    KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
    assert kafkaStreams != null;
    ReadOnlyKeyValueStore<String, AirQualityProcessedSchema> amounts = kafkaStreams
      .store(StoreQueryParameters.fromNameAndType("air-quality-state-store", QueryableStoreTypes.keyValueStore()));
    return amounts.get(id);
  }
 }

package com.mahoni.tripservice.trip.kafka;

import com.mahoni.schema.AirQualityProcessedSchema;
import com.mahoni.schema.UserPointTableSchema;
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
public class TripServiceStream {
  @Autowired
  StreamsBuilderFactoryBean factoryBean;

  @Autowired
  public KTable<String, UserPointTableSchema> userPointKtable(KStream<String, UserPointTableSchema> kStream) {
    return kStream.toTable(Materialized.as("user-point-state-store"));
  }

  @Autowired
  public KTable<String, UserPointTableSchema> airQualityKtable(KStream<String, UserPointTableSchema> kStream) {
    return kStream.toTable(Materialized.as("air-quality-state-store"));
  }

  public UserPointTableSchema getUserPoint(String id) {
    KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
    assert kafkaStreams != null;
    ReadOnlyKeyValueStore<String, UserPointTableSchema> amounts = kafkaStreams
      .store(StoreQueryParameters.fromNameAndType("user-point-state-store", QueryableStoreTypes.keyValueStore()));
    return amounts.get(id);
  }

  public AirQualityProcessedSchema getAirQuality(String id) {
    KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
    assert kafkaStreams != null;
    ReadOnlyKeyValueStore<String, AirQualityProcessedSchema> amounts = kafkaStreams
      .store(StoreQueryParameters.fromNameAndType("air-quality-state-store", QueryableStoreTypes.keyValueStore()));
    return amounts.get(id);
  }
 }

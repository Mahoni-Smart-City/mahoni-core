package com.mahoni.userservice.kafka;

import com.mahoni.schema.UserPointSchema;
import com.mahoni.schema.UserPointTableSchema;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class UserStreams {

  @Autowired
  StreamsBuilderFactoryBean factoryBean;

  @Autowired
  public void compactedTopic(KStream<String, UserPointSchema> userPointSchemaKStream) {
    KStream<String, UserPointTableSchema> userPointCompacted = userPointSchemaKStream
      .map((s, userPointSchema) -> KeyValue.pair(userPointSchema.getUserId(), UserPointTableSchema.newBuilder()
        .setUserId(userPointSchema.getUserId())
        .setPoint(userPointSchema.getPoint())
        .build()));
    userPointCompacted.to(KafkaTopic.USER_POINT_COMPACTED_TOPIC);
    userPointCompacted.toTable(Materialized.as("user-point-state-store"));
  }

//  @Autowired
//  public KTable<String, UserPointTableSchema> kTable(KStream<String, UserPointTableSchema> kStream) {
//    return kStream.toTable(Materialized.as("user-point-state-store"));
//  }
//    KGroupedStream<String, UserPointSchema> groupedUserPoint = userPointSchemaKStream
//      .selectKey((key, value) -> value.getUserId())
//      .groupByKey();
//
//    KTable<String, UserPointTableSchema> userPointTable = groupedUserPoint
//      .reduce((userPointSchema, v1) -> v1)
//      .mapValues((s, userPointSchema) -> UserPointTableSchema.newBuilder()
//        .setUserId(userPointSchema.getUserId())
//        .setPoint(userPointSchema.getPoint())
//        .build(), Materialized.as("user-point-state-store"));
//
//    userPointTable.toStream().to(KafkaTopic.USER_POINT_COMPACTED_TOPIC);

  public UserPointTableSchema get(String id) {
    KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
    assert kafkaStreams != null;
    ReadOnlyKeyValueStore<String, UserPointTableSchema> amounts = kafkaStreams
      .store(StoreQueryParameters.fromNameAndType("user-point-state-store", QueryableStoreTypes.keyValueStore()));
    return amounts.get(id);
  }
}

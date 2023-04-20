package com.mahoni.userservice.kafka;

import com.mahoni.schema.UserPointSchema;
import com.mahoni.schema.UserPointTableSchema;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class UserStreams {

  @Autowired
  StreamsBuilderFactoryBean factoryBean;

  @Autowired
  public void kTable(KStream<String, UserPointSchema> userPointSchemaKStream) {
    KGroupedStream<String, UserPointSchema> groupedUserPoint = userPointSchemaKStream
      .selectKey((key, value) -> value.getUserId())
      .groupByKey();

    KTable<String, UserPointTableSchema> userPointTable = groupedUserPoint
      .reduce((userPointSchema, v1) -> v1)
      .mapValues((s, userPointSchema) -> UserPointTableSchema.newBuilder()
        .setUserId(userPointSchema.getUserId())
        .setPoint(userPointSchema.getPoint())
        .build(), Materialized.as("user-point-state-store"));

    userPointTable.toStream().to(KafkaTopic.USER_POINT_COMPACTED_TOPIC);
  }

  public UserPointTableSchema get(String id) {
    KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
    assert kafkaStreams != null;
    ReadOnlyKeyValueStore<String, UserPointTableSchema> amounts = kafkaStreams
      .store(StoreQueryParameters.fromNameAndType("user-point-state-store", QueryableStoreTypes.keyValueStore()));
    return amounts.get(id);
  }
}

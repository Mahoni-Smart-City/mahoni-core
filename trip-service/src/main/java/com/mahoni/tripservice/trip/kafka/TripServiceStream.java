package com.mahoni.tripservice.trip.kafka;

import com.mahoni.schema.UserPointSchema;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class TripServiceStream {

  public static final String USER_POINT_COMPACTED_TOPIC = "user-point-compacted-topic";
  private static final Serde<String> stringSerde = Serdes.String();
  private static final SpecificAvroSerde<UserPointSchema> avroSerde =  new SpecificAvroSerde<>();
  @Value("${spring.kafka.schema.registry.url}")
  private String schemaRegistryUrl;

//  @Bean
//  @Autowired
//  public KTable<String, UserPointSchema> buildPipeline(StreamsBuilder streamsBuilder) {
//    Map<String, Object> serdeConfig = new HashMap<>();
//    serdeConfig.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
//
//    Serde<String> stringSerde = Serdes.String();
//    stringSerde.configure(serdeConfig, true);
//    SpecificAvroSerde<UserPointSchema> avroSerde =  new SpecificAvroSerde<>();
//    avroSerde.configure(serdeConfig, false);
//
//    StreamsBuilder streamsBuilder = new StreamsBuilder();
//    streamsBuilder.stream(USER_POINT_COMPACTED_TOPIC, Consumed.with(stringSerde, avroSerde))
//    return streamsBuilder
//      .stream(USER_POINT_COMPACTED_TOPIC, Consumed.with(stringSerde, avroSerde))
//      .toTable();


//    return streamsBuilder
//      .table(USER_POINT_COMPACTED_TOPIC, Materialized.as("user-point-store"));
//  }
}

package com.mahoni.tripservice.trip.configuration;

import com.mahoni.schema.UserPointSchema;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaConfiguration {

  @Value("${spring.kafka.bootstrap.servers}")
  private String bootstrapAddress;

  @Value("${spring.kafka.schema.registry.url}")
  private String schemaRegistryUrl;

  private static final Serde<String> stringSerde = Serdes.String();
  private static final SpecificAvroSerde<UserPointSchema> avroSerde =  new SpecificAvroSerde<>();

//  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
//  public KafkaStreamsConfiguration kafkaStreamsConfiguration() {
//    Map<String, Object> props = new HashMap<>();
//    props.put(APPLICATION_ID_CONFIG, "trip-streams");
//    props.put(GROUP_ID_CONFIG, "trip-streams-group-id");
//    props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
//    props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//    props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
//    props.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
//    props.put(STATE_DIR_CONFIG, "./data/trip-service/store");
//
//    return new KafkaStreamsConfiguration(props);
//  }
//
//  @Bean
//  public StreamsBuilder streamsBuilder() {
//    Map<String, Object> serdeConfig = new HashMap<>();
//    serdeConfig.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
//
//    Serde<String> stringSerde = Serdes.String();
//    stringSerde.configure(serdeConfig, true);
//    SpecificAvroSerde<UserPointSchema> avroSerde =  new SpecificAvroSerde<>();
//    avroSerde.configure(serdeConfig, false);
//
//    StreamsBuilder streamsBuilder = new StreamsBuilder();
//    streamsBuilder.stream(UserPointStream.USER_POINT_COMPACTED_TOPIC, Consumed.with(stringSerde, avroSerde));
//    return streamsBuilder;
//  }
}

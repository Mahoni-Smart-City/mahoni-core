package com.mahoni.tripservice.trip.config;

import com.mahoni.flink.schema.AirQualityProcessedSchema;
import com.mahoni.schema.UserPointSchema;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.*;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaConfiguration {

  @Value("${spring.kafka.bootstrap-servers}")
  private List<String> bootstrapAddress;
  @Value("${spring.kafka.properties.schema.registry.url}")
  private String schemaRegistryUrl;
  private static final Serde<String> stringSerde = Serdes.String();
  private static final SpecificAvroSerde<UserPointSchema> avroSerde =  new SpecificAvroSerde<>();
  public static final String USER_POINT_COMPACTED_TOPIC = "user-point-compacted-topic";
  public static final String AIR_QUALITY_COMPACTED_TOPIC = "air-quality-compacted-topic";

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  public KafkaStreamsConfiguration kafkaStreamsConfiguration() {
    Map<String, Object> props = new HashMap<>();
    props.put(APPLICATION_ID_CONFIG, "trip-streams");
    props.put(GROUP_ID_CONFIG, "trip-streams-group-id");
    props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
    props.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
    props.put(STATE_DIR_CONFIG, "./data/trip-service/store");

    return new KafkaStreamsConfiguration(props);
  }

  @Autowired
  @Bean
  public KTable<String, AirQualityProcessedSchema> buildPipelineAirQuality(StreamsBuilder streamsBuilder) {
    Map<String, Object> props = new HashMap<>();
    props.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
    Serde<String> stringSerde = Serdes.String();
    stringSerde.configure(props, true);
    SpecificAvroSerde<AirQualityProcessedSchema> avroSerde = new SpecificAvroSerde<>();
    avroSerde.configure(props, false);

    return streamsBuilder
      .table(AIR_QUALITY_COMPACTED_TOPIC, Consumed.with(stringSerde, avroSerde), Materialized.as("air-quality-state-store"));
  }
}

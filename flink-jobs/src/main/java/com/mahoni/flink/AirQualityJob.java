package com.mahoni.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class AirQualityJob {
  public static void main(String[] args) throws Exception{
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    // set up the Kafka consumer properties
    KafkaSource<String> source = KafkaSource.<String>builder()
      .setBootstrapServers("localhost:9092")
      .setTopics("air-quality-raw-topic")
      .setValueOnlyDeserializer(new SimpleStringSchema())
      .build();
    env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source").print();

    env.execute("Air Quality");
  }
}

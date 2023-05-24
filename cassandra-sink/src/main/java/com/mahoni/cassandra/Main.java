package com.mahoni.cassandra;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.mahoni.schema.AirQualityRawSchema;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.runtime.ConnectorConfig;
import org.apache.kafka.connect.runtime.WorkerConfig;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.sink.SinkTaskContext;
import org.apache.kafka.connect.storage.Converter;
import org.apache.kafka.connect.storage.ConverterType;
import org.apache.kafka.connect.storage.StringConverter;
import org.apache.kafka.connect.json.JsonConverter;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
//import com.datastax.oss.driver.api.core.CqlSessionBuilder;
//import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
/*
@SpringBootApplication
public class Main {
  private static final String CASSANDRA_KEYSPACE = "air_quality";
  private static final String CASSANDRA_TABLE = "air_sensor";
  private static final String CASSANDRA_HOST = "localhost";
  private static final int CASSANDRA_PORT = 9042;

  public static void main(String[] args) {

    ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

    //MessageProducer producer = context.getBean(MessageProducer.class);
    MessageListener listener = context.getBean(MessageListener.class);
    //listener.latch.await(10, TimeUnit.SECONDS);
    // Konfigurasi Kafka Connect
    /*
    Properties kafkaConsumerProps = new Properties();
    kafkaConsumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    kafkaConsumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
    kafkaConsumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
    kafkaConsumerProps.setProperty(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
    kafkaConsumerProps.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

     */

    // create Cassandra cluster and session
    /*
    CqlSessionBuilder builder = CqlSession.builder();
    builder.addContactPoint(InetSocketAddress.createUnresolved(CASSANDRA_HOST, CASSANDRA_PORT)).withLocalDatacenter("datacenter1");
    CqlSession session = builder.build();

     */
    //Schema key = Key.SCHEMA$;
    //Schema value = Value.SCHEMA$;
    //ConfluentRegistryAvroDeserializationSchema.forSpecific(AirQualityRawSchema.class,"http://localhost:8081")
/*
    Consumer<Key,Value> consumer = new KafkaConsumer<Key,Value>(kafkaConsumerProps);
    consumer.subscribe(Collections.singleton(KAFKA_TOPIC));
 */


/*
    // read and write data
    while (true) {
      ConsumerRecords<GenericRecord,GenericRecord> records = consumer.poll(100);
      for (ConsumerRecord<GenericRecord,GenericRecord> record : records) {
        Schema schema = record.value().getSchema();
        String name = schema.getName();
        String query = "INSERT INTO " + CASSANDRA_KEYSPACE + "." + CASSANDRA_TABLE + "(id,name_location,id_location) VALUES(" +
          record.value().getId() + "," +
          record.value().getNameLocation() + "," +
          record.value().getIdLocation() + ")";
        session.execute(query);
      }
    }

  }
  private static GenericRecord deserializeAvro(byte[] data, Schema SCHEMA) throws IOException {
    DatumReader<GenericRecord> reader = new org.apache.avro.generic.GenericDatumReader<>(SCHEMA);
    JsonDecoder decoder = DecoderFactory.get().jsonDecoder(SCHEMA, new String(data));
    return reader.read(null, decoder);
  }
  @Bean
  public MessageListener messageListener() {
    return new MessageListener();
  }

  public static class MessageListener {
    private CountDownLatch latch = new CountDownLatch(3);
    //private static final String KAFKA_TOPIC = "postgres.public.air_sensors";
    @KafkaListener(topics = "air-quality-raw-topic")
    public void test(AirQualityRawSchema airQualityRawSchema){
      /*
      System.out.print(air_sensor.getId());
      System.out.print(air_sensor.getNameLocation());
      System.out.print(air_sensor.getIdLocation());


      System.out.println(airQualityRawSchema.getAqi());
      latch.countDown();
    }
  }
}

 */


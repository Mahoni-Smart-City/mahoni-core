package com.mahoni.voucherservice.voucher.kafka;

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
public class VoucherServiceStream {
  @Autowired
  StreamsBuilderFactoryBean factoryBean;

  @Autowired
  public KTable<String, UserPointTableSchema> kTable(KStream<String, UserPointTableSchema> kStream) {
    return kStream.toTable(Materialized.as("user-point-state-store"));
  }

  public UserPointTableSchema get(String id) {
    KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
    assert kafkaStreams != null;
    ReadOnlyKeyValueStore<String, UserPointTableSchema> amounts = kafkaStreams
      .store(StoreQueryParameters.fromNameAndType("user-point-state-store", QueryableStoreTypes.keyValueStore()));
    return amounts.get(id);
  }
}

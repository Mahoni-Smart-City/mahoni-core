package com.mahoni.userservice.mock;
import com.mahoni.schema.TripSchema;
import com.mahoni.schema.UserPointSchema;
import com.mahoni.schema.VoucherRedeemedSchema;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;

public class CustomKafkaAvroDeserializer extends KafkaAvroDeserializer {

  @Override
  public Object deserialize(String topic, byte[] bytes) {
    if (topic.equals("trip-topic")) {
      this.schemaRegistry = getMockClient(TripSchema.SCHEMA$);
    }
    if (topic.equals("voucher-redeemed-topic")) {
      this.schemaRegistry = getMockClient(VoucherRedeemedSchema.SCHEMA$);
    }
    if (topic.equals("user-point-topic")) {
      this.schemaRegistry = getMockClient(UserPointSchema.SCHEMA$);
    }
    return super.deserialize(topic, bytes);
  }

  private static SchemaRegistryClient getMockClient(final Schema schema$) {
    return new MockSchemaRegistryClient() {
      @Override
      public synchronized Schema getById(int id) {
        return schema$;
      }
    };
  }
}

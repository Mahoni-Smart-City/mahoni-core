package com.mahoni.userservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopic {

    @Bean
    public NewTopic testTopic() {
        return TopicBuilder.name("test-topic")
                .replicas(1)
                .build();
    }
}

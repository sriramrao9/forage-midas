package com.jpmc.midascore;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestKafkaTopicConfig {

    @Bean
    public NewTopic transactionsTopic() {
        return new NewTopic("transactions", 1, (short) 1);
    }
}

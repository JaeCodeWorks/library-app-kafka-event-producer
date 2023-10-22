package com.company.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConfig {

    @Autowired
    private LibraryListenerConfig libraryListenerConfig;

    @Bean
    public KafkaAdmin kafkaAdmin() {

        Map<String, Object> kafkaAdminConfigMap = new HashMap<>();
        kafkaAdminConfigMap.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                libraryListenerConfig.getLibraryKafkaAdminBootstrapServers());

        return new KafkaAdmin(kafkaAdminConfigMap);
    }
}

package com.company.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class LibraryListenerConfig {

    private String libraryTopicName;
    private int libraryTopicPartitions;
    private short libraryTopicReplicationFactor;
    private String libraryKafkaAdminBootstrapServers;
    private Map<String, String> libraryKafkaProducerProperties;
}

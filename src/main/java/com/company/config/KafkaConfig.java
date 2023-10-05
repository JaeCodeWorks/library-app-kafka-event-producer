package com.company.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Autowired
    private LibraryListenerConfiguration libraryListenerConfiguration;

    @Bean
    public KafkaTemplate<Integer, String> kafkaLibraryTemplate() {

        return new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<Integer, String>(
                        new HashMap(libraryListenerConfiguration.getLibraryKafkaProducerProperties())));
    }
}

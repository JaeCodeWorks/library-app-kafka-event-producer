package com.company.config;

import com.company.producer.LibraryEventsProducer;
import com.company.producer.LibraryEventsProducerImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@EnableKafka
@Configuration
public class KafKaLibraryProducerConfig {

    @Autowired
    private LibraryListenerConfig libraryListenerConfig;

    @Bean
    public KafkaTemplate<Integer, String> kafkaLibraryTemplate() {

        return new KafkaTemplate<Integer, String>(
                new DefaultKafkaProducerFactory(libraryListenerConfig.getLibraryKafkaProducerProperties()));
    }

    @Bean
    public LibraryEventsProducer libraryEventsProducer() {

        return new LibraryEventsProducerImpl(
                libraryListenerConfig.getLibraryTopicName(),
                kafkaLibraryTemplate(),
                new ObjectMapper());
    }


    @Bean
    public NewTopic libraryTopic() {

        String libraryTopicName = libraryListenerConfig.getLibraryTopicName();
        int libraryTopicPartition = libraryListenerConfig.getLibraryTopicPartitions();
        short libraryTopicReplicationFactor = libraryListenerConfig.getLibraryTopicReplicationFactor();

        log.info("Creating topic bean name:{}, partition:{}, replicationFactor:{}",
                libraryTopicName,
                libraryTopicPartition,
                libraryTopicReplicationFactor);

        return new NewTopic(libraryTopicName, libraryTopicPartition, libraryTopicReplicationFactor);
    }
}

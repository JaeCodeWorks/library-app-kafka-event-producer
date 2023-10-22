package com.company.producer;

import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.company.model.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class LibraryEventsProducerImpl implements LibraryEventsProducer {

    private final String libraryTopic;
    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String SEND_EVENT_MSG = "Sending event to topic:{} with key:{} value:{}";

    @Override
    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent)
            throws JsonProcessingException {

        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);

        log.info(SEND_EVENT_MSG, libraryTopic, key, value);

        //1. when call happens for the first time, it's a blocking call - get metadata about Kafka cluster
        //2. Asynchronous call: Then send message happens - (returns a CompletableFuture)
        var completableFuture = kafkaTemplate.send(buildProducerRecordWithHeaders(key, value));

        return completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, value, throwable);
                    } else {
                        handleSuccess(key, value, sendResult);
                    }
                });

    }

    private ProducerRecord<Integer, String> buildProducerRecordWithHeaders(Integer key, String value) {

        List<Header> recordHeaders = List.of(new RecordHeader(
                "sourceSystemId",
                "library-app-kafka-events-producer".getBytes()));

        return new ProducerRecord<>(libraryTopic, null, key, value, recordHeaders);
    }

    private void handleSuccess(int key, String value, SendResult<Integer, String> sendResult) {
        log.info("Event sent successfully for key:{} value:{}, for topic{} partition:{} with offset:{}",
                key, value,
                sendResult.getRecordMetadata().topic(),
                sendResult.getRecordMetadata().partition(),
                sendResult.getRecordMetadata().offset());
    }

    private void handleFailure(int key, String value, Throwable ex) {

        log.error("Error while sending the event for key:{} value:{}, {}",
                key, value, ex.getMessage(),
                ex);
    }
}

package com.company.producer;

import com.company.model.LibraryEvent;
import com.company.util.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.company.util.TestUtils.KAFKA_LIBRARY_TOPIC_V1;
import static com.company.util.TestUtils.LIBRARY_EVENT_ID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class LibraryEventsProducerUnitTest {

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplate;

    ObjectMapper objectMapper;

    CompletableFuture<SendResult<Integer, String>> completableFuture;

    SendResult<Integer, String> expectedSendResult;

    ProducerRecord<Integer, String> producerRecord;

    RecordMetadata recordMetadata;

    LibraryEventsProducerImpl testObj;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();
        testObj = new LibraryEventsProducerImpl(KAFKA_LIBRARY_TOPIC_V1, kafkaTemplate, objectMapper);

    }

    @SneakyThrows
    @Test
    void sendLibraryEvent_successfulOnCompletableFuture_assertExpectedSendResult() {

        //given
        LibraryEvent libraryEvent = TestUtils.newLibraryEventRecordWithLibraryEventId();
        producerRecord = buildProducerRecord(objectMapper.writeValueAsString(libraryEvent));
        recordMetadata = buildRecordMetadata();
        expectedSendResult = new SendResult<>(producerRecord, recordMetadata);
        completableFuture = CompletableFuture.completedFuture(expectedSendResult);

        when(kafkaTemplate.send(producerRecord)).thenReturn(completableFuture);

        //when
        var actualCompletableFuture = testObj.sendLibraryEvent(libraryEvent);

        //then
        verify(kafkaTemplate, times(1)).send(producerRecord);
        assertThat(expectedSendResult).isEqualTo(actualCompletableFuture.get());
        assertThat(expectedSendResult.getRecordMetadata().partition()).isEqualTo(1);
    }


    @SneakyThrows
    @Test
    void sendLibraryEvent_exceptionOnCompletableFuture_completeFutureWithExceptionIsTrue() {

        LibraryEvent libraryEvent = TestUtils.newLibraryEventRecordWithLibraryEventId();
        producerRecord = buildProducerRecord(objectMapper.writeValueAsString(libraryEvent));
        recordMetadata = buildRecordMetadata();
        expectedSendResult = new SendResult<>(producerRecord, recordMetadata);

        completableFuture = CompletableFuture.failedFuture(new RuntimeException("Exception Calling Kafka"));

        when(kafkaTemplate.send(isA(ProducerRecord.class)))
                .thenReturn(CompletableFuture.supplyAsync(() ->
                        completableFuture));

        var actualCompletableFuture = testObj.sendLibraryEvent(libraryEvent);

        assertThatThrownBy(actualCompletableFuture::get).isInstanceOf(Exception.class);
        verify(kafkaTemplate, times(1)).send(producerRecord);
        assertThat(actualCompletableFuture.isCompletedExceptionally()).isTrue();
    }

    private RecordMetadata buildRecordMetadata() {
        return new RecordMetadata(new TopicPartition(KAFKA_LIBRARY_TOPIC_V1, 1),
                1, 1, System.currentTimeMillis(), 1, 2);
    }

    private ProducerRecord<Integer, String> buildProducerRecord(String record) {

        List<Header> recordHeaders = List.of(new RecordHeader(
                "sourceSystemId",
                "library-app-kafka-events-producer".getBytes()));

        return new ProducerRecord<>(KAFKA_LIBRARY_TOPIC_V1, null, LIBRARY_EVENT_ID, record, recordHeaders);
    }
}
package com.company.controller;

import com.company.model.ErrorResponse;
import com.company.model.LibraryEvent;
import com.company.model.LibraryEventType;
import com.company.util.Fixtures;
import com.company.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static com.company.util.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = KAFKA_LIBRARY_TOPIC_V1)
@TestPropertySource(properties = {
        "kafka-config.library-kafka-producer-properties.bootstrap.servers=${spring.embedded.kafka.brokers}",
        "kafka-config.library-kafka-admin-bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ObjectMapper objectMapper;

    private Consumer<Integer, String> consumer;

    private ResponseEntity<LibraryEvent> libraryResponseEntity;
    private ResponseEntity<ErrorResponse> errorResponseEntity;

    private static final String METHOD_ARGUMENT_NOT_VALID_CODE = "argument.not.valid";
    private static final String BAD_REQUEST_ERROR_CODE = "library-event-producer.bad-request.error";

    @BeforeEach
    void setUp() {

        setupConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {

        consumer.close();
    }

    @Test
    void addBook_validLibraryEvent_successfulResponse() {

        //given
        final LibraryEvent expectedLibraryEvent = TestUtils.newLibraryEventRecord();

        //when
        libraryApiCallSuccessful(expectedLibraryEvent, HttpMethod.POST);

        //then
        assertThat(HttpStatus.CREATED).isEqualTo(libraryResponseEntity.getStatusCode());
        assertThat(expectedLibraryEvent).isEqualTo(libraryResponseEntity.getBody());

        assertRecordFromConsumer(expectedLibraryEvent);
    }

    @Test
    void addBook_validLibraryEventWithKey_successfulResponse() {

        final LibraryEvent expectedLibraryEvent = TestUtils.newLibraryEventRecordWithLibraryEventId();

        libraryApiCallSuccessful(expectedLibraryEvent, HttpMethod.POST);

        assertThat(HttpStatus.CREATED).isEqualTo(libraryResponseEntity.getStatusCode());
        assertThat(expectedLibraryEvent).isEqualTo(libraryResponseEntity.getBody());

        assertRecordFromConsumer(expectedLibraryEvent);
    }

    @Test
    void addBook_bookNameIsMissing_badRequest() {

        final String expectedErrorMessage = "book.bookName-must not be blank";

        String request = Fixtures.fixture("fixtures/library_event_request_payload.json",
                getRequestPlaceHolders(LIBRARY_EVENT_ID, LibraryEventType.NEW.name(),
                        BOOK_ID, "", BOOK_AUTHOR));

        libraryApiCallWithFixtureUnsuccessful(request, HttpMethod.POST);

        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(errorResponseEntity.getStatusCode());
        assertThat(METHOD_ARGUMENT_NOT_VALID_CODE).isEqualTo(errorResponseEntity.getBody().errorCode());
        assertThat(expectedErrorMessage).isEqualTo(errorResponseEntity.getBody().errorMessage());
    }

    @Test
    void addBook_bookAuthorIsMissing_badRequest() {

        final String expectedErrorMessage = "book.bookAuthor-must not be blank";

        String request = Fixtures.fixture("fixtures/library_event_request_payload.json",
                getRequestPlaceHolders(LIBRARY_EVENT_ID, LibraryEventType.NEW.name(),
                        BOOK_ID, BOOK_NAME, ""));

        libraryApiCallWithFixtureUnsuccessful(request, HttpMethod.POST);

        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(errorResponseEntity.getStatusCode());
        assertThat(METHOD_ARGUMENT_NOT_VALID_CODE).isEqualTo(errorResponseEntity.getBody().errorCode());
        assertThat(expectedErrorMessage).isEqualTo(errorResponseEntity.getBody().errorMessage());
    }

    @Test
    void updateLibrary_validLibraryEvent_successfulResponse() {

        final LibraryEvent expectedLibraryEvent = TestUtils.libraryEventRecordUpdate();

        libraryApiCallSuccessful(expectedLibraryEvent, HttpMethod.PUT);

        assertThat(HttpStatus.CREATED).isEqualTo(libraryResponseEntity.getStatusCode());
        assertThat(expectedLibraryEvent).isEqualTo(libraryResponseEntity.getBody());

        assertRecordFromConsumer(expectedLibraryEvent);
    }

    @Test
    void updateLibrary_libraryEventIdNull_successfulResponse() {

        final LibraryEvent expectedLibraryEvent = TestUtils.libraryEventRecordUpdateWithNullLibraryEventId();

        libraryApiCallSuccessful(expectedLibraryEvent, HttpMethod.PUT);

        assertThat(HttpStatus.CREATED).isEqualTo(libraryResponseEntity.getStatusCode());
        assertThat(expectedLibraryEvent).isEqualTo(libraryResponseEntity.getBody());

        assertRecordFromConsumer(expectedLibraryEvent);
    }

    @Test
    void updateLibrary_libraryEventTypeNew_badRequest() {

        libraryApiCallUnsuccessful(TestUtils.newLibraryEventRecordWithLibraryEventId(), HttpMethod.PUT);

        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(errorResponseEntity.getStatusCode());
        assertThat(BAD_REQUEST_ERROR_CODE).isEqualTo(errorResponseEntity.getBody().errorCode());
        assertThat(LIBRARY_UPDATE_TYPE_ERROR_MESSAGE).isEqualTo(errorResponseEntity.getBody().errorMessage());
    }

    @Test
    void updateLibrary_bookIdIsNull_badRequest() {

        libraryApiCallUnsuccessful(TestUtils.libraryEventRecordWithInvalidBook(), HttpMethod.PUT);

        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(errorResponseEntity.getStatusCode());
        assertThat(METHOD_ARGUMENT_NOT_VALID_CODE).isEqualTo(errorResponseEntity.getBody().errorCode());
        assertThat(ERROR_MSG_INVALID_BOOK_ID_AND_AUTHOR).isEqualTo(errorResponseEntity.getBody().errorMessage());
    }

    private void assertRecordFromConsumer(LibraryEvent expectedLibraryEvent) {

        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);
        assertThat(consumerRecords.count()).isEqualTo(1);

        consumerRecords.forEach(record -> {
            LibraryEvent actualLibraryEvent = TestUtils.parseLibraryEventRecord(objectMapper, record.value());
            assertThat(actualLibraryEvent).isEqualTo(expectedLibraryEvent);
        });
    }

    private void setupConsumer() {

        Map<String, Object> configs = KafkaTestUtils
                .consumerProps("group1", "true", embeddedKafkaBroker);

        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        consumer = new DefaultKafkaConsumerFactory<>(
                configs,
                new IntegerDeserializer(),
                new StringDeserializer())
                .createConsumer();
    }

    private HttpHeaders getDefaultHttpHeaders() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return httpHeaders;
    }

    private void libraryApiCallSuccessful(LibraryEvent libraryEvent, HttpMethod httpMethod) {

        libraryResponseEntity = restTemplate.exchange(
                BASE_URL_PATH,
                httpMethod,
                new HttpEntity<>(libraryEvent, getDefaultHttpHeaders()),
                LibraryEvent.class);
    }


    private void libraryApiCallUnsuccessful(LibraryEvent libraryEvent, HttpMethod httpMethod) {

        errorResponseEntity = restTemplate.exchange(
                BASE_URL_PATH,
                httpMethod,
                new HttpEntity<>(libraryEvent, getDefaultHttpHeaders()),
                ErrorResponse.class);
    }

    private static ImmutableMap<String, Object> getRequestPlaceHolders(Integer libraryEventId, String libraryEventType,
                                                                       Integer bookId, String bookName, String bookAuthor) {
        return ImmutableMap.<String, Object>of(
                "libraryEventId", libraryEventId,
                "libraryEventType", libraryEventType,
                "bookId", bookId,
                "bookName", bookName,
                "bookAuthor", bookAuthor
        );
    }

    private void libraryApiCallWithFixtureUnsuccessful(String requestPayload, HttpMethod httpMethod) {

        errorResponseEntity = restTemplate.exchange(
                BASE_URL_PATH,
                httpMethod,
                new HttpEntity<>(requestPayload, getDefaultHttpHeaders()),
                ErrorResponse.class);
    }
}
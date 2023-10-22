package com.company.util;

import com.company.model.Book;
import com.company.model.LibraryEvent;
import com.company.model.LibraryEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

    public static final String BASE_URL_PATH = "/v1/libraryevent";
    public static final String KAFKA_LIBRARY_TOPIC_V1 = "public.library.collection.v1";

    public static final Integer LIBRARY_EVENT_ID = 995;
    public static final Integer BOOK_ID = 456;
    public static final String BOOK_NAME = "Head First Design Patterns";
    public static final String BOOK_AUTHOR = "Eric Freeman";

    public static final String LIBRARY_EVENT_ID_REQUIRED_ERROR_MESSAGE = "LibraryEventId required, has not been provided";
    public static final String LIBRARY_UPDATE_TYPE_ERROR_MESSAGE = "Only 'UPDATE' event type is supported";

    public static final String ERROR_MSG_INVALID_BOOK_ID_AND_AUTHOR = "book.bookAuthor-must not be blank, book.bookId-must not be null";

    public static LibraryEvent newLibraryEventRecord() {
        return new LibraryEvent(null, LibraryEventType.NEW, bookRecord());
    }

    public static LibraryEvent newLibraryEventRecordWithLibraryEventId() {
        return new LibraryEvent(LIBRARY_EVENT_ID, LibraryEventType.NEW, bookRecord());
    }

    public static LibraryEvent libraryEventRecordWithInvalidBook() {
        return new LibraryEvent(LIBRARY_EVENT_ID, LibraryEventType.NEW, bookRecordWithInvalidValues());
    }

    public static LibraryEvent libraryEventRecordUpdate() {
        return new LibraryEvent(LIBRARY_EVENT_ID, LibraryEventType.UPDATE, bookRecord());
    }

    public static LibraryEvent libraryEventRecordUpdateWithNullLibraryEventId() {
        return new LibraryEvent(null, LibraryEventType.UPDATE, bookRecord());
    }

    public static LibraryEvent parseLibraryEventRecord(ObjectMapper objectMapper, String json) {
        try {
            return objectMapper.readValue(json, LibraryEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Book bookRecord() {
        return new Book(BOOK_ID, BOOK_NAME, BOOK_AUTHOR);
    }

    public static Book bookRecordWithInvalidValues() {
        return new Book(null, BOOK_NAME, "");
    }
}

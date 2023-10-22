package com.company.exception;

public class BadRequestException extends LibraryEventProducerException {

    public static final String LIBRARY_EVENT_PRODUCER_BAD_REQUEST_CODE =
            "library-event-producer.bad-request.error";

    public BadRequestException(String message) {

        super(LIBRARY_EVENT_PRODUCER_BAD_REQUEST_CODE, message);
    }
}

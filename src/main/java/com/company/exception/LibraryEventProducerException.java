package com.company.exception;

import lombok.Getter;

@Getter
public class LibraryEventProducerException extends RuntimeException {

    private static final String LIBRARY_EVENT_PRODUCER_ERROR_CODE = "library-event-producer.default.error";

    private final String errorCode;

    public LibraryEventProducerException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public LibraryEventProducerException(String message) {
        super(message);
        this.errorCode = LIBRARY_EVENT_PRODUCER_ERROR_CODE;
    }

    public LibraryEventProducerException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = LIBRARY_EVENT_PRODUCER_ERROR_CODE;
    }

    public LibraryEventProducerException(Throwable cause) {
        super(cause);
        this.errorCode = LIBRARY_EVENT_PRODUCER_ERROR_CODE;
    }
}

package com.company.producer;

import com.company.model.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface LibraryEventsProducer {

    CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException;
}

package com.company.controller;

import com.company.exception.BadRequestException;
import com.company.model.LibraryEvent;
import com.company.model.LibraryEventType;
import com.company.producer.LibraryEventsProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class LibraryEventsController {

    private final LibraryEventsProducer libraryEventsProducer;

    @PostMapping("/libraryevent")
    public ResponseEntity<LibraryEvent> addBook(@RequestBody @Valid LibraryEvent libraryEvent)
            throws JsonProcessingException {

        log.info("Library create event received libraryEvent:{}", libraryEvent);

        libraryEventsProducer.sendLibraryEvent(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("/libraryevent")
    public ResponseEntity<?> updateLibrary(@RequestBody @Valid LibraryEvent libraryEvent)
            throws JsonProcessingException {

        log.info("Library update event received libraryEvent:{}", libraryEvent);

        validateUpdateLibraryEvent(libraryEvent);

        libraryEventsProducer.sendLibraryEvent(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    private void validateUpdateLibraryEvent(LibraryEvent libraryEvent) {

        if(!libraryEvent.libraryEventType().equals(LibraryEventType.UPDATE)) {
            throw new BadRequestException("Only 'UPDATE' event type is supported");
        }
    }

}

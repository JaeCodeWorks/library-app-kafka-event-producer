package com.company.controller;

import com.company.model.LibraryEvent;
import com.company.producer.LibraryEventsProducer;
import com.company.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.company.util.TestUtils.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LibraryEventsController.class)
class LibraryEventsControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LibraryEventsProducer libraryEventsProducer;


    @SneakyThrows
    @BeforeEach
    void setup() {

        when(libraryEventsProducer.sendLibraryEvent(isA(LibraryEvent.class)))
                .thenReturn(null);
    }

    @SneakyThrows
    @Test
    void addBook_validLibraryEvent_isSuccessful() {

        mockMvc.perform(post(BASE_URL_PATH)
                        .content(objectMapper.writeValueAsString(TestUtils.newLibraryEventRecord()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }

    @SneakyThrows
    @Test
    void addBook_libraryEventInvalidBook_isBadRequest() {

        mockMvc.perform(post(BASE_URL_PATH)
                        .content(objectMapper.writeValueAsString(TestUtils.libraryEventRecordWithInvalidBook()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value(ERROR_MSG_INVALID_BOOK_ID_AND_AUTHOR));
    }

    @SneakyThrows
    @Test
    void updateLibrary_validLibraryEvent_isSuccessful() {

        mockMvc.perform(put(BASE_URL_PATH)
                        .content(objectMapper.writeValueAsString(TestUtils.libraryEventRecordUpdate()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @SneakyThrows
    @Test
    void updateLibrary_libraryEventIdIsMissing_isSuccessful() {

        mockMvc.perform(put(BASE_URL_PATH)
                        .content(objectMapper.writeValueAsString(TestUtils.libraryEventRecordUpdateWithNullLibraryEventId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @SneakyThrows
    @Test
    void updateLibrary_invalidBook_isBadRequest() {

        mockMvc.perform(put(BASE_URL_PATH)
                        .content(objectMapper.writeValueAsString(TestUtils.libraryEventRecordWithInvalidBook()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value(ERROR_MSG_INVALID_BOOK_ID_AND_AUTHOR));
    }

    @SneakyThrows
    @Test
    void updateLibrary_libraryEventTypeNew_isBadRequest() {

        mockMvc.perform(put(BASE_URL_PATH)
                        .content(objectMapper.writeValueAsString(TestUtils.newLibraryEventRecordWithLibraryEventId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value(LIBRARY_UPDATE_TYPE_ERROR_MESSAGE));
    }
}
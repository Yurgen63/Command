package org.skypro.star.controller;

import org.junit.jupiter.api.Test;
import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    RecommendationService service;

    @Test
    void returnsJsonWithUserIdAndRecommendations() throws Exception {
        UUID userId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");
        when(service.getRecommendations(userId)).thenReturn(List.of(
                new RecommendationDto("Invest 500", UUID.randomUUID(), "text")));

        mockMvc.perform(get("/recommendation/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user_id").value(userId.toString()))
                .andExpect(jsonPath("$.recommendations[0].name").value("Invest 500"));
    }

    @Test
    void returnsEmptyArrayWhenNoRecommendations() throws Exception {
        UUID userId = UUID.randomUUID();
        when(service.getRecommendations(userId)).thenReturn(List.of());

        mockMvc.perform(get("/recommendation/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userId.toString()))
                .andExpect(jsonPath("$.recommendations").isEmpty());
    }
}
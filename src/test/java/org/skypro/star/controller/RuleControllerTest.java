package org.skypro.star.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skypro.star.dto.rule.QueryDto;
import org.skypro.star.dto.rule.RuleDto;
import org.skypro.star.dto.rule.RuleStatsDto;
import org.skypro.star.exception.RuleNotFoundException;
import org.skypro.star.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RuleController.class)
class RuleControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean RuleService service;

    private RuleDto sampleRule() {
        return new RuleDto(
                1L,
                "Test product",
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "text",
                List.of(new QueryDto("USER_OF", List.of("DEBIT"), false))
        );
    }

    @Test
    void createReturnsSavedRule() throws Exception {
        RuleDto dto = sampleRule();
        when(service.create(any(RuleDto.class))).thenReturn(dto);

        mockMvc.perform(post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.product_name").value("Test product"))
                .andExpect(jsonPath("$.rule[0].query").value("USER_OF"));
    }

    @Test
    void findAllReturnsListResponse() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleRule()));

        mockMvc.perform(get("/rule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].product_name").value("Test product"));
    }

    @Test
    void statsReturnsCounts() throws Exception {
        when(service.getStats()).thenReturn(List.of(new RuleStatsDto("1", "5")));

        mockMvc.perform(get("/rule/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats[0].rule_id").value("1"))
                .andExpect(jsonPath("$.stats[0].count").value("5"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/rule/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturnsNotFoundWhenRuleMissing() throws Exception {
        doThrow(new RuleNotFoundException(99L)).when(service).delete(eq(99L));

        mockMvc.perform(delete("/rule/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
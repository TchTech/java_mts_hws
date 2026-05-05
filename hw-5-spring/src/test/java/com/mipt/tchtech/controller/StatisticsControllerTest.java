package com.mipt.tchtech.controller;

import java.util.List;

import com.mipt.tchtech.dto.PriorityStatisticsDto;
import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.service.TaskStatisticsJdbcService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskStatisticsJdbcService statisticsService;

    @Test
    void getCountByPriority_returns200AndJsonBody() throws Exception {
        List<PriorityStatisticsDto> stats = List.of(
                new PriorityStatisticsDto(Priority.HIGH, 7L),
                new PriorityStatisticsDto(Priority.MEDIUM, 3L));

        given(statisticsService.getTasksCountByPriority()).willReturn(stats);

        mockMvc.perform(get("/api/statistics/by-priority")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[0].count").value(7))
                .andExpect(jsonPath("$[1].priority").value("MEDIUM"))
                .andExpect(jsonPath("$[1].count").value(3));

        verify(statisticsService).getTasksCountByPriority();
    }
}

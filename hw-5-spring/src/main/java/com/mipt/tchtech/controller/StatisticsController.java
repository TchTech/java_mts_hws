package com.mipt.tchtech.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mipt.tchtech.dto.PriorityStatisticsDto;
import com.mipt.tchtech.service.TaskStatisticsJdbcService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics")
public class StatisticsController {

    private final TaskStatisticsJdbcService statisticsService;

    public StatisticsController(TaskStatisticsJdbcService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/by-priority")
    @Operation(summary = "Количество задач по приоритетам (JdbcTemplate)")
    public ResponseEntity<List<PriorityStatisticsDto>> getCountByPriority() {
        return ResponseEntity.ok(statisticsService.getTasksCountByPriority());
    }
}

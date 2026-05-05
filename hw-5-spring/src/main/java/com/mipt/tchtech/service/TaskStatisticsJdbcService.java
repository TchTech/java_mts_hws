package com.mipt.tchtech.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.mipt.tchtech.dto.PriorityStatisticsDto;
import com.mipt.tchtech.model.Priority;

@Service
public class TaskStatisticsJdbcService {

    private final JdbcTemplate jdbcTemplate;

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PriorityStatisticsDto> getTasksCountByPriority() {
        String sql = "SELECT priority, COUNT(*) AS task_count FROM tasks GROUP BY priority ORDER BY priority";
        RowMapper<PriorityStatisticsDto> mapper = (rs, rowNum) -> new PriorityStatisticsDto(
                Priority.valueOf(rs.getString("priority")),
                rs.getLong("task_count")
        );
        return jdbcTemplate.query(sql, mapper);
    }
}

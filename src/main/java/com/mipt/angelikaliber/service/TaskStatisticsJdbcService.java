package com.mipt.angelikaliber.service;

import com.mipt.angelikaliber.dto.PriorityCountDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatisticsJdbcService {

    private final JdbcTemplate jdbcTemplate;

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PriorityCountDto> getTasksCountByPriority() {
        String sql = "SELECT COALESCE(priority, 'NONE') AS priority, COUNT(*) AS cnt "
                + "FROM tasks GROUP BY priority ORDER BY priority";

        RowMapper<PriorityCountDto> mapper = (rs, rowNum) ->
                new PriorityCountDto(rs.getString("priority"), rs.getLong("cnt"));

        return jdbcTemplate.query(sql, mapper);
    }
}

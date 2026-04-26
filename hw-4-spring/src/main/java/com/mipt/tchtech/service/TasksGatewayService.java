package com.mipt.tchtech.service;

import com.mipt.tchtech.client.ExternalTasksClient;
import com.mipt.tchtech.dto.ExternalTaskDto;
import com.mipt.tchtech.exception.TaskNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TasksGatewayService {

    private static final Logger log = LoggerFactory.getLogger(TasksGatewayService.class);

    private final ExternalTasksClient client;

    public TasksGatewayService(ExternalTasksClient client) {
        this.client = client;
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "createTaskFallback")
    @RateLimiter(name = "externalApi", fallbackMethod = "createTaskFallback")
    public ExternalTaskDto createTask(ExternalTaskDto dto) {
        return client.createTask(dto);
    }

    private ExternalTaskDto createTaskFallback(ExternalTaskDto dto, Throwable t) {
        log.warn("createTask fallback: {}", t.getMessage());
        ExternalTaskDto stub = new ExternalTaskDto();
        stub.setId(-1L);
        stub.setTitle("[UNAVAILABLE] " + dto.getTitle());
        stub.setDescription("Внешний сервис недоступен — попробуйте позже");
        stub.setCompleted(false);
        return stub;
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTaskFallback")
    @RateLimiter(name = "externalApi", fallbackMethod = "getTaskFallback")
    public ExternalTaskDto getTask(long id) {
        return client.getTask(id);
    }

    private ExternalTaskDto getTaskFallback(long id, Throwable t) {
        if (t instanceof TaskNotFoundException ex) {
            throw ex;
        }
        log.warn("getTask fallback for id={}: {}", id, t.getMessage());
        ExternalTaskDto stub = new ExternalTaskDto();
        stub.setId(id);
        stub.setTitle("[UNAVAILABLE]");
        stub.setDescription("Внешний сервис недоступен — попробуйте позже");
        stub.setCompleted(false);
        return stub;
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "listTasksFallback")
    @RateLimiter(name = "externalApi", fallbackMethod = "listTasksFallback")
    public List<ExternalTaskDto> listTasks(Boolean completed, Integer limit) {
        return client.listTasks(completed, limit);
    }

    private List<ExternalTaskDto> listTasksFallback(Boolean completed, Integer limit, Throwable t) {
        log.warn("listTasks fallback: {}", t.getMessage());
        return Collections.emptyList();
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteTaskFallback")
    @RateLimiter(name = "externalApi", fallbackMethod = "deleteTaskFallback")
    public void deleteTask(long id) {
        client.deleteTask(id);
    }

    private void deleteTaskFallback(long id, Throwable t) {
        if (t instanceof TaskNotFoundException ex) {
            throw ex;
        }
        log.warn("deleteTask fallback for id={}: {}", id, t.getMessage());
    }
}
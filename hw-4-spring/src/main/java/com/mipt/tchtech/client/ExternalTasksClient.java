package com.mipt.tchtech.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.tchtech.dto.ExternalProblemDetail;
import com.mipt.tchtech.dto.ExternalTaskDto;
import com.mipt.tchtech.exception.ExternalApiException;
import com.mipt.tchtech.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class ExternalTasksClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);
    private static final String TASKS_PATH = "/external/v1/tasks";
    private static final int MAX_ERROR_BODY_BYTES = 500;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ExternalTasksClient(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public ExternalTaskDto createTask(ExternalTaskDto dto) {
        ResponseEntity<ExternalTaskDto> response = restClient.post()
                .uri(TASKS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .toEntity(ExternalTaskDto.class);

        if (response.getStatusCode().value() == 201) {
            URI location = response.getHeaders().getLocation();
            if (location != null) {
                log.info("External task created, Location: {}", location);
            }
        }
        return response.getBody();
    }

    public ExternalTaskDto getTask(long id) {
        return restClient.get()
                .uri(TASKS_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, this::handle404)
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(ExternalTaskDto.class);
    }

    public List<ExternalTaskDto> listTasks(Boolean completed, Integer limit) {
        return restClient.get()
                .uri(builder -> {
                    builder.path(TASKS_PATH);
                    if (completed != null) builder.queryParam("completed", completed);
                    if (limit != null) builder.queryParam("limit", limit);
                    return builder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<List<ExternalTaskDto>>() {});
    }

    public void deleteTask(long id) {
        restClient.delete()
                .uri(TASKS_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, this::handle404)
                .onStatus(HttpStatusCode::isError, this::handleError)
                .toBodilessEntity();
    }

    private void handle404(org.springframework.http.HttpRequest request,
                           ClientHttpResponse response) throws IOException {
        MediaType contentType = response.getHeaders().getContentType();
        byte[] body = response.getBody().readAllBytes();

        if (contentType != null && contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            try {
                ExternalProblemDetail problem = objectMapper.readValue(body, ExternalProblemDetail.class);
                String detail = problem.getDetail() != null ? problem.getDetail() : "Task not found";
                throw new TaskNotFoundException(detail);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse 404 ProblemDetail: {}", e.getMessage());
                throw new TaskNotFoundException("Task not found");
            }
        } else {
            String limitedBody = new String(body, 0, Math.min(body.length, MAX_ERROR_BODY_BYTES), StandardCharsets.UTF_8);
            log.warn("External API 404 with unexpected content-type={}, body(limited): {}", contentType, limitedBody);
            throw new TaskNotFoundException("Task not found");
        }
    }

    private void handleError(org.springframework.http.HttpRequest request,
                             ClientHttpResponse response) throws IOException {
        int status = response.getStatusCode().value();
        MediaType contentType = response.getHeaders().getContentType();

        if (contentType == null || !contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            byte[] body = response.getBody().readNBytes(MAX_ERROR_BODY_BYTES);
            String limitedBody = new String(body, StandardCharsets.UTF_8);
            log.warn("External API error status={}, unexpected content-type={}, body(limited): {}",
                    status, contentType, limitedBody);
            throw new ExternalApiException("External API returned non-JSON response, status: " + status);
        }

        throw new ExternalApiException("External API error, status: " + status);
    }
}
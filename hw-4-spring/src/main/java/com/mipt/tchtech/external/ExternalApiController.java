package com.mipt.tchtech.external;

import com.mipt.tchtech.dto.ExternalTaskDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiController.class);

    private final ExternalTaskStore store;

    public ExternalApiController(ExternalTaskStore store) {
        this.store = store;
    }

    @PostMapping("/tasks")
    public ResponseEntity<ExternalTaskDto> createTask(@RequestBody ExternalTaskDto dto,
                                                      HttpServletRequest request) {
        ExternalTaskDto created = store.save(dto);
        URI location = URI.create(request.getRequestURL().toString() + "/" + created.getId());
        log.debug("External: created task id={}, location={}", created.getId(), location);
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable long id) {
        return store.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(problemDetail(404, "Task Not Found",
                                "Задача с id=" + id + " не найдена",
                                "/external/v1/tasks/" + id)));
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<ExternalTaskDto>> listTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit) {
        List<ExternalTaskDto> result = store.findAll(completed);
        if (limit != null) {
            result = result.stream().limit(limit).collect(Collectors.toList());
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable long id) {
        if (!store.deleteById(id)) {
            return ResponseEntity.status(404)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(problemDetail(404, "Task Not Found",
                            "Задача с id=" + id + " не найдена",
                            "/external/v1/tasks/" + id));
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam(defaultValue = "500") String mode) {
        log.info("External unstable endpoint called with mode={}", mode);
        return switch (mode) {
            case "timeout" -> {
                try {
                    Thread.sleep(30_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                yield ResponseEntity.ok(Map.of("result", "ok"));
            }
            case "500" -> ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(problemDetail(500, "Internal Server Error",
                            "Simulated server error", "/external/v1/unstable"));
            case "429" -> ResponseEntity.status(429)
                    .header("Retry-After", "5")
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(problemDetail(429, "Too Many Requests",
                            "Rate limit exceeded, retry after 5 seconds", "/external/v1/unstable"));
            case "html" -> ResponseEntity.status(502)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body><h1>502 Bad Gateway</h1><p>Upstream service unavailable</p></body></html>");
            default -> ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(problemDetail(400, "Bad Request",
                            "Unknown mode: " + mode + ". Use: timeout|500|429|html",
                            "/external/v1/unstable"));
        };
    }

    private Map<String, Object> problemDetail(int status, String title, String detail, String instance) {
        Map<String, Object> problem = new LinkedHashMap<>();
        problem.put("type", "about:blank");
        problem.put("title", title);
        problem.put("status", status);
        problem.put("detail", detail);
        problem.put("instance", instance);
        return problem;
    }
}
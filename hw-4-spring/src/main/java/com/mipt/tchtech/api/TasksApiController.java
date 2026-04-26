package com.mipt.tchtech.api;

import com.mipt.tchtech.dto.ExternalTaskDto;
import com.mipt.tchtech.service.TasksGatewayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TasksApiController {

    private static final Logger log = LoggerFactory.getLogger(TasksApiController.class);

    private final TasksGatewayService gatewayService;

    public TasksApiController(TasksGatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @PostMapping
    public ResponseEntity<ExternalTaskDto> createTask(@RequestBody ExternalTaskDto dto) {
        log.info("Gateway: createTask title='{}'", dto.getTitle());
        ExternalTaskDto created = gatewayService.createTask(dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExternalTaskDto> getTask(@PathVariable long id) {
        log.info("Gateway: getTask id={}", id);
        return ResponseEntity.ok(gatewayService.getTask(id));
    }

    @GetMapping
    public ResponseEntity<List<ExternalTaskDto>> listTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit) {
        log.info("Gateway: listTasks completed={} limit={}", completed, limit);
        return ResponseEntity.ok(gatewayService.listTasks(completed, limit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable long id) {
        log.info("Gateway: deleteTask id={}", id);
        gatewayService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
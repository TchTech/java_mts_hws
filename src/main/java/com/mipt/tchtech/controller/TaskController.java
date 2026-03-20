package com.mipt.tchtech.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mipt.tchtech.config.RequestScopedBean;
import com.mipt.tchtech.dto.TaskCreateDto;
import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.dto.TaskUpdateDto;
import com.mipt.tchtech.exception.TaskNotFoundException;
import com.mipt.tchtech.service.TaskService;
import com.mipt.tchtech.validation.OnCreate;
import com.mipt.tchtech.validation.OnUpdate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Управление задачами")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;
    private final ApplicationContext applicationContext;

    @Value("${api.version:2.0.0}")
    private String apiVersion;

    public TaskController(TaskService taskService, ApplicationContext applicationContext) {
        this.taskService = taskService;
        this.applicationContext = applicationContext;
    }

    @GetMapping
    @Operation(summary = "Получить все задачи")
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        RequestScopedBean requestBean = applicationContext.getBean(RequestScopedBean.class);
        log.info("TaskController.getAllTasks вызван, ID запроса: {}", requestBean.getRequestId());
        List<TaskResponseDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .header("X-API-Version", apiVersion)
                .body(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить задачу по ID")
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok()
                        .header("X-API-Version", apiVersion)
                        .body(task))
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @PostMapping
    @Operation(summary = "Создать задачу")
    public ResponseEntity<TaskResponseDto> createTask(
            @Validated(OnCreate.class) @RequestBody TaskCreateDto dto) {
        TaskResponseDto created = taskService.createTask(dto);
        return ResponseEntity.status(201)
                .header("X-API-Version", apiVersion)
                .body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить задачу")
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable String id,
            @Validated(OnUpdate.class) @RequestBody TaskUpdateDto dto) {
        return taskService.updateTask(id, dto)
                .map(task -> ResponseEntity.ok()
                        .header("X-API-Version", apiVersion)
                        .body(task))
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить задачу")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent()
                .header("X-API-Version", apiVersion)
                .build();
    }
}

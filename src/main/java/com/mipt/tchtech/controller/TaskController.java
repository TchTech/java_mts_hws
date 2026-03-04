package com.mipt.tchtech.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mipt.tchtech.config.RequestScopedBean;
import com.mipt.tchtech.dto.TaskDto;
import com.mipt.tchtech.service.TaskService;

/**
 * REST контроллер для управления задачами.
 * Предоставляет API для выполнения CRUD операций над объектами TaskDto.
 *
 * @author mts.tchtech
 * @version 1.0
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;
    private final ApplicationContext applicationContext;

    public TaskController(TaskService taskService, ApplicationContext applicationContext) {
        this.taskService = taskService;
        this.applicationContext = applicationContext;
    }

    @GetMapping
    public List<TaskDto> getAllTasks() {
        RequestScopedBean requestBean = applicationContext.getBean(RequestScopedBean.class);
        log.info("TaskController.getAllTasks вызван, ID запроса: {}", requestBean.getRequestId());
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TaskDto createTask(@RequestBody TaskDto task) {
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable String id, @RequestBody TaskDto task) {
        return taskService.updateTask(id, task)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}

package com.mipt.tchtech.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.mipt.tchtech.config.PrototypeScopedBean;
import com.mipt.tchtech.dto.TaskDto;
import com.mipt.tchtech.dto.TaskMapper;
import com.mipt.tchtech.model.TaskEntity;
import com.mipt.tchtech.repository.TaskRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Сервис для управления бизнес-логикой задач.
 * Инкапсулирует работу с репозиторием и предоставляет методы для создания, чтения, обновления и удаления задач.
 *
 * @author mts.tchtech
 * @version 1.0
 */
@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final ApplicationContext applicationContext;
    private final TaskMapper taskMapper;

    @Value("${app.name:Неизвестное приложение}")
    private String appName;

    public TaskService(TaskRepository taskRepository, ApplicationContext applicationContext, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.applicationContext = applicationContext;
        this.taskMapper = taskMapper;
    }

    @PostConstruct
    public void initCache() {
        log.info("TaskService @PostConstruct: Инициализация кэша для приложения: {}", appName);
        taskRepository.save(new TaskEntity("init-1", "Первая задача", "Описание 1", false));
        taskRepository.save(new TaskEntity("init-2", "Вторая задача", "Описание 2", true));
    }

    @PreDestroy
    public void cleanup() {
        long count = taskRepository.findAll().size();
        log.info("TaskService @PreDestroy: Очистка ресурсов. Задач в кэше перед уничтожением: {}", count);
    }

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<TaskDto> getTaskById(String id) {
        return taskRepository.findById(id).map(taskMapper::toDto);
    }

    public TaskDto createTask(TaskDto task) {
        if (task.getId() == null || task.getId().isEmpty()) {
            PrototypeScopedBean idGenerator = applicationContext.getBean(PrototypeScopedBean.class);
            task.setId(idGenerator.getGeneratedId());
        }
        TaskEntity entity = taskMapper.toEntity(task);
        return taskMapper.toDto(taskRepository.save(entity));
    }

    public Optional<TaskDto> updateTask(String id, TaskDto updatedTask) {
        return taskRepository.findById(id).map(existingTask -> {
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setCompleted(updatedTask.isCompleted());
            return taskMapper.toDto(taskRepository.save(existingTask));
        });
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }
}

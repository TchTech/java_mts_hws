package com.mipt.tchtech.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.mipt.tchtech.config.PrototypeScopedBean;
import com.mipt.tchtech.dto.TaskCreateDto;
import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.dto.TaskUpdateDto;
import com.mipt.tchtech.mapper.TaskMapper;
import com.mipt.tchtech.model.TaskEntity;
import com.mipt.tchtech.repository.TaskRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

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

    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<TaskResponseDto> getTaskById(String id) {
        return taskRepository.findById(id).map(taskMapper::toResponseDto);
    }

    public TaskResponseDto createTask(TaskCreateDto dto) {
        TaskEntity entity = taskMapper.toEntity(dto);
        PrototypeScopedBean idGenerator = applicationContext.getBean(PrototypeScopedBean.class);
        entity.setId(idGenerator.getGeneratedId());
        entity.setCreatedAt(LocalDateTime.now());
        return taskMapper.toResponseDto(taskRepository.save(entity));
    }

    public Optional<TaskResponseDto> updateTask(String id, TaskUpdateDto dto) {
        return taskRepository.findById(id).map(existingTask -> {
            taskMapper.updateEntity(dto, existingTask);
            return taskMapper.toResponseDto(taskRepository.save(existingTask));
        });
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }
}

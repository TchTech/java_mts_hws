package com.mipt.tchtech.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mipt.tchtech.dto.TaskCreateDto;
import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.dto.TaskUpdateDto;
import com.mipt.tchtech.exception.BulkOperationException;
import com.mipt.tchtech.exception.TaskNotFoundException;
import com.mipt.tchtech.mapper.TaskMapper;
import com.mipt.tchtech.model.TaskEntity;
import com.mipt.tchtech.repository.TaskRepository;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Value("${app.name:Неизвестное приложение}")
    private String appName;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<TaskResponseDto> getTaskById(Long id) {
        return taskRepository.findById(id).map(taskMapper::toResponseDto);
    }

    public List<TaskResponseDto> getUpcomingTasks() {
        LocalDate today = LocalDate.now();
        return taskRepository.findUpcoming(today, today.plusDays(7)).stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDto> getAllTasksWithAttachments() {
        return taskRepository.findAllWithAttachments().stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponseDto createTask(TaskCreateDto dto) {
        TaskEntity entity = taskMapper.toEntity(dto);
        entity.setCompleted(false);
        if (entity.getTags() == null) {
            entity.setTags(new HashSet<>());
        }
        return taskMapper.toResponseDto(taskRepository.save(entity));
    }

    @Transactional
    public Optional<TaskResponseDto> updateTask(Long id, TaskUpdateDto dto) {
        return taskRepository.findById(id).map(existing -> {
            taskMapper.updateEntity(dto, existing);
            return taskMapper.toResponseDto(taskRepository.save(existing));
        });
    }

    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = {BulkOperationException.class, TaskNotFoundException.class},
            timeout = 10
    )
    public void bulkCompleteTasks(List<Long> ids) {
        log.info("Пакетное завершение задач: {}", ids);
        List<TaskEntity> tasks = taskRepository.findAllById(ids);

        Set<Long> foundIds = tasks.stream().map(TaskEntity::getId).collect(Collectors.toSet());
        List<Long> missing = new ArrayList<>();
        for (Long requested : ids) {
            if (!foundIds.contains(requested)) {
                missing.add(requested);
            }
        }
        if (!missing.isEmpty()) {
            throw new BulkOperationException(missing);
        }

        for (TaskEntity task : tasks) {
            task.setCompleted(true);
        }
        taskRepository.saveAll(tasks);
    }
}

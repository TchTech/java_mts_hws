package com.mipt.tchtech.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.mipt.tchtech.config.PrototypeScopedBean;
import com.mipt.tchtech.model.Task;
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

    @Value("${app.name:Неизвестное приложение}")
    private String appName;

    public TaskService(TaskRepository taskRepository, ApplicationContext applicationContext) {
        this.taskRepository = taskRepository;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void initCache() {
        log.info("TaskService @PostConstruct: Инициализация кэша для приложения: {}", appName);
        taskRepository.save(new Task("init-1", "Первая задача", "Описание 1", false));
        taskRepository.save(new Task("init-2", "Вторая задача", "Описание 2", true));
    }

    @PreDestroy
    public void cleanup() {
        long count = taskRepository.findAll().size();
        log.info("TaskService @PreDestroy: Очистка ресурсов. Задач в кэше перед уничтожением: {}", count);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        if (task.getId() == null || task.getId().isEmpty()) {
            PrototypeScopedBean idGenerator = applicationContext.getBean(PrototypeScopedBean.class);
            task.setId(idGenerator.getGeneratedId());
        }
        return taskRepository.save(task);
    }

    public Optional<Task> updateTask(String id, Task updatedTask) {
        return taskRepository.findById(id).map(existingTask -> {
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setCompleted(updatedTask.isCompleted());
            return taskRepository.save(existingTask);
        });
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }
}

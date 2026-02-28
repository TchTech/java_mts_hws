package com.mipt.tchtech.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.mipt.tchtech.model.Task;

/**
 * Реализация репозитория задач в памяти.
 * Использует ConcurrentHashMap для потокобезопасного хранения задач.
 * Является основной реализацией репозитория по умолчанию.
 *
 * @author mts.tchtech
 * @version 1.0
 */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task save(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteById(String id) {
        tasks.remove(id);
    }

    public Map<String, Task> getTasksMap() {
        return tasks;
    }
}

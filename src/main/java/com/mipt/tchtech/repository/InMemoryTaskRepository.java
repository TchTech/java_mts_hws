package com.mipt.tchtech.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.mipt.tchtech.model.TaskEntity;

@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<String, TaskEntity> tasks = new ConcurrentHashMap<>();

    @Override
    public Optional<TaskEntity> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<TaskEntity> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public TaskEntity save(TaskEntity task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteById(String id) {
        tasks.remove(id);
    }

    public Map<String, TaskEntity> getTasksMap() {
        return tasks;
    }
}

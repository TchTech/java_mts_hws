package com.mipt.tchtech.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mipt.tchtech.model.TaskEntity;

public class StubTaskRepository implements TaskRepository {

    private final List<TaskEntity> stubTasks;

    public StubTaskRepository() {
        stubTasks = new ArrayList<>();
        stubTasks.add(new TaskEntity("stub-1", "Заглушка 1", "Описание для заглушки 1", false));
        stubTasks.add(new TaskEntity("stub-2", "Заглушка 2", "Описание для заглушки 2", true));
    }

    @Override
    public Optional<TaskEntity> findById(String id) {
        return stubTasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<TaskEntity> findAll() {
        return new ArrayList<>(stubTasks);
    }

    @Override
    public TaskEntity save(TaskEntity task) {
        return task;
    }

    @Override
    public void deleteById(String id) {
    }
}

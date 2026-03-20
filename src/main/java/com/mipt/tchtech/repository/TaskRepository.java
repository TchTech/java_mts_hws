package com.mipt.tchtech.repository;

import java.util.List;
import java.util.Optional;

import com.mipt.tchtech.model.TaskEntity;

public interface TaskRepository {

    Optional<TaskEntity> findById(String id);

    List<TaskEntity> findAll();

    TaskEntity save(TaskEntity task);

    void deleteById(String id);
}

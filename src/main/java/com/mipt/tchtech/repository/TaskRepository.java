package com.mipt.tchtech.repository;

import java.util.List;
import java.util.Optional;

import com.mipt.tchtech.model.TaskEntity;

/**
 * Интерфейс репозитория для управления сущностями TaskEntity.
 * Определяет базовые операции для работы с хранилищем задач.
 *
 * @author mts.tchtech
 * @version 1.0
 */
public interface TaskRepository {

    Optional<TaskEntity> findById(String id);

    List<TaskEntity> findAll();

    TaskEntity save(TaskEntity task);

    void deleteById(String id);
}

package com.mipt.tchtech.repository;

import java.util.List;
import java.util.Optional;

import com.mipt.tchtech.model.Task;

/**
 * Интерфейс репозитория для управления сущностями Task.
 * Определяет базовые операции для работы с хранилищем задач.
 *
 * @author mts.tchtech
 * @version 1.0
 */
public interface TaskRepository {

    Optional<Task> findById(String id);

    List<Task> findAll();

    Task save(Task task);

    void deleteById(String id);
}

package com.mipt.tchtech.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mipt.tchtech.model.Task;

/**
 * Репозиторий-заглушка для задач.
 * Предоставляет статический набор данных для тестирования и отладки,
 * не сохраняет новые задачи и не удаляет существующие.
 *
 * @author mts.tchtech
 * @version 1.0
 */
public class StubTaskRepository implements TaskRepository {

    private final List<Task> stubTasks;

    public StubTaskRepository() {
        stubTasks = new ArrayList<>();
        stubTasks.add(new Task("stub-1", "Заглушка 1", "Описание для заглушки 1", false));
        stubTasks.add(new Task("stub-2", "Заглушка 2", "Описание для заглушки 2", true));
    }

    @Override
    public Optional<Task> findById(String id) {
        return stubTasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(stubTasks);
    }

    @Override
    public Task save(Task task) {
        return task;
    }

    @Override
    public void deleteById(String id) {
    }
}

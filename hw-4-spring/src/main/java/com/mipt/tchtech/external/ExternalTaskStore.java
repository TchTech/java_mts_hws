package com.mipt.tchtech.external;

import com.mipt.tchtech.dto.ExternalTaskDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class ExternalTaskStore {

    private final Map<Long, ExternalTaskDto> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public ExternalTaskDto save(ExternalTaskDto task) {
        if (task.getId() == null) {
            task.setId(idSequence.getAndIncrement());
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public Optional<ExternalTaskDto> findById(long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public List<ExternalTaskDto> findAll(Boolean completed) {
        return tasks.values().stream()
                .filter(t -> completed == null || t.isCompleted() == completed)
                .collect(Collectors.toList());
    }

    public boolean deleteById(long id) {
        return tasks.remove(id) != null;
    }
}
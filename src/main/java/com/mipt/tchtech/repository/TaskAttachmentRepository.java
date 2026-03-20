package com.mipt.tchtech.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.mipt.tchtech.model.TaskAttachment;

@Repository
public class TaskAttachmentRepository {

    private final Map<Long, TaskAttachment> store = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public TaskAttachment save(TaskAttachment attachment) {
        if (attachment.getId() == null) {
            attachment.setId(idCounter.getAndIncrement());
        }
        store.put(attachment.getId(), attachment);
        return attachment;
    }

    public Optional<TaskAttachment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<TaskAttachment> findByTaskId(String taskId) {
        return store.values().stream()
                .filter(a -> a.getTaskId().equals(taskId))
                .collect(Collectors.toList());
    }

    public List<TaskAttachment> findAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}

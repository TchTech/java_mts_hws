package com.mipt.tchtech.dto;

import org.springframework.stereotype.Component;

import com.mipt.tchtech.model.TaskEntity;

@Component
public class TaskMapper {

    public TaskDto toDto(TaskEntity entity) {
        if (entity == null) {
            return null;
        }
        return new TaskDto(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.isCompleted()
        );
    }

    public TaskEntity toEntity(TaskDto dto) {
        if (dto == null) {
            return null;
        }
        return new TaskEntity(
                dto.getId(),
                dto.getTitle(),
                dto.getDescription(),
                dto.isCompleted()
        );
    }
}

package com.mipt.tchtech.mapper;

import java.util.LinkedHashSet;
import java.util.Set;

import com.mipt.tchtech.dto.TaskCreateDto;
import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.dto.TaskUpdateDto;
import com.mipt.tchtech.model.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public TaskEntity toEntity(TaskCreateDto dto) {
        if (dto == null) {
            return null;
        }

        TaskEntity taskEntity = new TaskEntity();

        taskEntity.setTitle(dto.getTitle());
        taskEntity.setDescription(dto.getDescription());
        taskEntity.setDueDate(dto.getDueDate());
        taskEntity.setPriority(dto.getPriority());
        Set<String> set = dto.getTags();
        if (set != null) {
            taskEntity.setTags(new LinkedHashSet<>(set));
        }

        return taskEntity;
    }

    @Override
    public void updateEntity(TaskUpdateDto dto, TaskEntity task) {
        if (dto == null) {
            return;
        }

        if (dto.getTitle() != null) {
            task.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }
        if (dto.getCompleted() != null) {
            task.setCompleted(dto.getCompleted());
        }
        if (dto.getDueDate() != null) {
            task.setDueDate(dto.getDueDate());
        }
        if (dto.getPriority() != null) {
            task.setPriority(dto.getPriority());
        }
        if (task.getTags() != null) {
            Set<String> set = dto.getTags();
            if (set != null) {
                task.getTags().clear();
                task.getTags().addAll(set);
            }
        } else {
            Set<String> set = dto.getTags();
            if (set != null) {
                task.setTags(new LinkedHashSet<>(set));
            }
        }
    }

    @Override
    public TaskResponseDto toResponseDto(TaskEntity task) {
        if (task == null) {
            return null;
        }

        TaskResponseDto taskResponseDto = new TaskResponseDto();

        taskResponseDto.setId(task.getId());
        taskResponseDto.setUpdatedAt(task.getUpdatedAt());
        taskResponseDto.setTitle(task.getTitle());
        taskResponseDto.setDescription(task.getDescription());
        taskResponseDto.setCompleted(task.isCompleted());
        taskResponseDto.setCreatedAt(task.getCreatedAt());
        taskResponseDto.setDueDate(task.getDueDate());
        taskResponseDto.setPriority(task.getPriority());
        Set<String> set = task.getTags();
        if (set != null) {
            taskResponseDto.setTags(new LinkedHashSet<>(set));
        }

        return taskResponseDto;
    }
}

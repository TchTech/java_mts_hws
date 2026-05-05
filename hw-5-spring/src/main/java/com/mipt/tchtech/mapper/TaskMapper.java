package com.mipt.tchtech.mapper;

import com.mipt.tchtech.dto.TaskCreateDto;
import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.dto.TaskUpdateDto;
import com.mipt.tchtech.model.TaskEntity;

public interface TaskMapper {

    TaskEntity toEntity(TaskCreateDto dto);

    void updateEntity(TaskUpdateDto dto, TaskEntity task);

    TaskResponseDto toResponseDto(TaskEntity task);
}

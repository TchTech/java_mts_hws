package com.mipt.tchtech.mapper;

import com.mipt.tchtech.dto.TaskCreateDto;
import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.dto.TaskUpdateDto;
import com.mipt.tchtech.model.TaskEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskEntity toEntity(TaskCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TaskUpdateDto dto, @MappingTarget TaskEntity task);

    TaskResponseDto toResponseDto(TaskEntity task);
}

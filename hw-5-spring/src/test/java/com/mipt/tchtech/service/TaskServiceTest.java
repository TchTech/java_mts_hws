package com.mipt.tchtech.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.dto.TaskUpdateDto;
import com.mipt.tchtech.mapper.TaskMapper;
import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.model.TaskEntity;
import com.mipt.tchtech.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private TaskMapper taskMapper;

    @Test
    void updateTask_existingTask_savesUpdatedEntityAndReturnsDto() {
        Long taskId = 42L;

        TaskEntity existing = new TaskEntity("Старое название", "Описание", false);
        existing.setId(taskId);
        existing.setPriority(Priority.LOW);
        existing.setTags(new HashSet<>());
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));

        TaskUpdateDto updateDto = new TaskUpdateDto();
        updateDto.setTitle("Новое название");
        updateDto.setCompleted(true);
        updateDto.setPriority(Priority.HIGH);

        TaskResponseDto expectedResponse = new TaskResponseDto();
        expectedResponse.setId(taskId);
        expectedResponse.setTitle("Новое название");
        expectedResponse.setCompleted(true);
        expectedResponse.setPriority(Priority.HIGH);

        given(taskRepository.findById(taskId)).willReturn(Optional.of(existing));
        given(taskRepository.save(any(TaskEntity.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(taskMapper.toResponseDto(any(TaskEntity.class))).willReturn(expectedResponse);

        Optional<TaskResponseDto> result = taskService.updateTask(taskId, updateDto);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(taskId);
        assertThat(result.get().isCompleted()).isTrue();
        assertThat(result.get().getTitle()).isEqualTo("Новое название");

        verify(taskRepository, times(1)).findById(taskId);

        ArgumentCaptor<TaskEntity> entityCaptor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepository, times(1)).save(entityCaptor.capture());
        TaskEntity savedEntity = entityCaptor.getValue();
        assertThat(savedEntity.getId()).isEqualTo(taskId);

        verify(taskMapper, times(1)).updateEntity(updateDto, existing);
        verify(taskMapper, times(1)).toResponseDto(any(TaskEntity.class));
    }

    @Test
    void updateTask_missingTask_returnsEmptyAndNeverSaves() {
        Long missingId = 999L;
        given(taskRepository.findById(missingId)).willReturn(Optional.empty());

        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setCompleted(true);

        Optional<TaskResponseDto> result = taskService.updateTask(missingId, dto);

        assertThat(result).isEmpty();
        verify(taskRepository, times(1)).findById(missingId);
        verify(taskRepository, never()).save(any(TaskEntity.class));
        verify(taskMapper, never()).updateEntity(any(), any());
    }
}

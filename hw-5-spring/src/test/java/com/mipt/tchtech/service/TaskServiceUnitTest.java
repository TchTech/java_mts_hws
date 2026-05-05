package com.mipt.tchtech.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.mipt.tchtech.dto.TaskCreateDto;
import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.exception.BulkOperationException;
import com.mipt.tchtech.mapper.TaskMapper;
import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.model.TaskEntity;
import com.mipt.tchtech.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskServiceUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getAllTasks_returnsMappedDtos() {
        TaskEntity e1 = new TaskEntity("t1", "d1", false);
        e1.setId(1L);
        TaskEntity e2 = new TaskEntity("t2", "d2", true);
        e2.setId(2L);
        TaskResponseDto r1 = new TaskResponseDto();
        r1.setId(1L);
        TaskResponseDto r2 = new TaskResponseDto();
        r2.setId(2L);

        given(taskRepository.findAll()).willReturn(Arrays.asList(e1, e2));
        given(taskMapper.toResponseDto(e1)).willReturn(r1);
        given(taskMapper.toResponseDto(e2)).willReturn(r2);

        List<TaskResponseDto> result = taskService.getAllTasks();

        assertThat(result).extracting(TaskResponseDto::getId).containsExactly(1L, 2L);
        verify(taskRepository, times(1)).findAll();
        verify(taskMapper, times(2)).toResponseDto(any(TaskEntity.class));
    }

    @Test
    void createTask_savesEntityWithCompletedFalseAndEmptyTags() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Task");
        dto.setPriority(Priority.MEDIUM);

        TaskEntity entityFromMapper = new TaskEntity("Task", null, false);
        entityFromMapper.setPriority(Priority.MEDIUM);

        TaskResponseDto expected = new TaskResponseDto();
        expected.setId(10L);
        expected.setTitle("Task");

        given(taskMapper.toEntity(dto)).willReturn(entityFromMapper);
        given(taskRepository.save(any(TaskEntity.class))).willAnswer(inv -> {
            TaskEntity e = inv.getArgument(0);
            e.setId(10L);
            return e;
        });
        given(taskMapper.toResponseDto(any(TaskEntity.class))).willReturn(expected);

        TaskResponseDto result = taskService.createTask(dto);

        assertThat(result.getId()).isEqualTo(10L);

        ArgumentCaptor<TaskEntity> saved = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepository).save(saved.capture());
        assertThat(saved.getValue().isCompleted()).isFalse();
        assertThat(saved.getValue().getTags()).isEmpty();
    }

    @Test
    void deleteTask_delegatesToRepository() {
        taskService.deleteTask(42L);
        verify(taskRepository, times(1)).deleteById(42L);
    }

    @Test
    void getTaskById_existing_returnsMappedDto() {
        TaskEntity entity = new TaskEntity("title", "desc", false);
        entity.setId(5L);
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(5L);

        given(taskRepository.findById(5L)).willReturn(Optional.of(entity));
        given(taskMapper.toResponseDto(entity)).willReturn(dto);

        Optional<TaskResponseDto> result = taskService.getTaskById(5L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(5L);
    }

    @Test
    void bulkCompleteTasks_marksAllProvided_andSavesThem() {
        TaskEntity a = new TaskEntity("a", "", false);
        a.setId(1L);
        TaskEntity b = new TaskEntity("b", "", false);
        b.setId(2L);
        List<Long> ids = Arrays.asList(1L, 2L);

        given(taskRepository.findAllById(ids)).willReturn(Arrays.asList(a, b));

        taskService.bulkCompleteTasks(ids);

        assertThat(a.isCompleted()).isTrue();
        assertThat(b.isCompleted()).isTrue();
        verify(taskRepository, times(1)).saveAll(anyList());
    }

    @Test
    void bulkCompleteTasks_missingId_throwsBulkOperationException_andDoesNotSave() {
        List<Long> ids = Arrays.asList(1L, 999L);
        TaskEntity a = new TaskEntity("a", "", false);
        a.setId(1L);

        given(taskRepository.findAllById(ids)).willReturn(Collections.singletonList(a));

        assertThatThrownBy(() -> taskService.bulkCompleteTasks(ids))
                .isInstanceOf(BulkOperationException.class);

        verify(taskRepository, never()).saveAll(anyList());
    }
}

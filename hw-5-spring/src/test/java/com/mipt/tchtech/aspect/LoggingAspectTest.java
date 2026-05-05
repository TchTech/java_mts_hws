package com.mipt.tchtech.aspect;

import java.util.Optional;

import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.mapper.TaskMapper;
import com.mipt.tchtech.repository.TaskRepository;
import com.mipt.tchtech.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class LoggingAspectTest {

    @Autowired
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        given(taskRepository.findById(any())).willReturn(Optional.empty());
    }

    @Test
    void taskServiceBeanIsWrappedByAopProxy() {
        assertThat(taskService.getClass().getName())
                .as("Сервис должен быть обёрнут AOP-проксёй (LoggingAspect)")
                .contains("SpringCGLIB");
    }

    @Test
    void serviceCall_passesThroughAspectWithoutChangingResult() {
        Optional<TaskResponseDto> result = taskService.getTaskById(123L);

        assertThat(result).isEmpty();
    }
}

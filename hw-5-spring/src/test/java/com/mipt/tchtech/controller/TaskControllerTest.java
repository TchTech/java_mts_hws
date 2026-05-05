package com.mipt.tchtech.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.tchtech.config.RequestScopedBean;
import com.mipt.tchtech.dto.TaskCreateDto;
import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.dto.TaskUpdateDto;
import com.mipt.tchtech.exception.GlobalExceptionHandler;
import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private RequestScopedBean requestScopedBean;

    @Test
    void createTask_validPayload_returns201AndJsonBody() throws Exception {
        TaskCreateDto request = new TaskCreateDto();
        request.setTitle("Купить хлеб");
        request.setDescription("В ближайшем магазине");
        request.setPriority(Priority.HIGH);
        request.setDueDate(LocalDate.now().plusDays(2));
        request.setTags(Set.of("дом"));

        TaskResponseDto saved = new TaskResponseDto();
        saved.setId(11L);
        saved.setTitle("Купить хлеб");
        saved.setDescription("В ближайшем магазине");
        saved.setCompleted(false);
        saved.setPriority(Priority.HIGH);
        saved.setDueDate(LocalDate.now().plusDays(2));
        saved.setCreatedAt(LocalDateTime.now());
        Set<String> tags = new HashSet<>();
        tags.add("дом");
        saved.setTags(tags);

        given(taskService.createTask(any(TaskCreateDto.class))).willReturn(saved);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.title").value("Купить хлеб"))
                .andExpect(jsonPath("$.description").value("В ближайшем магазине"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.tags", hasSize(1)));

        verify(taskService).createTask(any(TaskCreateDto.class));
    }

    @Test
    void createTask_invalidPayload_returns400ValidationError() throws Exception {
        String invalidJson = "{\"title\":\"a\",\"description\":\"...\"}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void getTaskById_existingTask_returns200AndDto() throws Exception {
        long taskId = 7L;

        TaskResponseDto stored = new TaskResponseDto();
        stored.setId(taskId);
        stored.setTitle("Полить цветы");
        stored.setDescription("По расписанию");
        stored.setCompleted(false);
        stored.setPriority(Priority.MEDIUM);
        stored.setDueDate(LocalDate.now().plusDays(1));
        stored.setCreatedAt(LocalDateTime.now().minusHours(1));
        Set<String> tags = new HashSet<>();
        tags.add("дом");
        tags.add("растения");
        stored.setTags(tags);

        given(taskService.getTaskById(eq(taskId))).willReturn(Optional.of(stored));

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value((int) taskId))
                .andExpect(jsonPath("$.title").value("Полить цветы"))
                .andExpect(jsonPath("$.description").value("По расписанию"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.tags", hasSize(2)));

        verify(taskService).getTaskById(taskId);
    }

    @Test
    void getAllTasks_returns200WithTotalCountHeader() throws Exception {
        TaskResponseDto t = new TaskResponseDto();
        t.setId(1L);
        t.setTitle("One");
        given(taskService.getAllTasks()).willReturn(List.of(t));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(header().exists("X-API-Version"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("One"));

        verify(taskService).getAllTasks();
    }

    @Test
    void updateTask_validPayload_returns200AndJson() throws Exception {
        TaskUpdateDto body = new TaskUpdateDto();
        body.setTitle("Новый заголовок");
        body.setCompleted(true);

        TaskResponseDto updated = new TaskResponseDto();
        updated.setId(5L);
        updated.setTitle("Новый заголовок");
        updated.setCompleted(true);
        updated.setPriority(Priority.LOW);

        given(taskService.updateTask(eq(5L), any(TaskUpdateDto.class)))
                .willReturn(Optional.of(updated));

        mockMvc.perform(put("/api/tasks/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("Новый заголовок"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(taskService).updateTask(eq(5L), any(TaskUpdateDto.class));
    }

    @Test
    void updateTask_invalidTitle_returns400() throws Exception {
        String invalidJson = "{\"title\":\"ab\",\"completed\":false}";

        mockMvc.perform(put("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void getTaskById_unknownId_returns404Problem() throws Exception {
        given(taskService.getTaskById(404L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/{id}", 404L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createTask_emptyTags_serializedAsExpected() throws Exception {
        TaskCreateDto request = new TaskCreateDto();
        request.setTitle("Без тегов");
        request.setPriority(Priority.MEDIUM);
        request.setTags(Collections.emptySet());

        TaskResponseDto saved = new TaskResponseDto();
        saved.setId(20L);
        saved.setTitle("Без тегов");
        saved.setCompleted(false);
        saved.setPriority(Priority.MEDIUM);
        saved.setTags(Collections.emptySet());

        given(taskService.createTask(any(TaskCreateDto.class))).willReturn(saved);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tags").isEmpty());
    }
}

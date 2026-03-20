package com.mipt.tchtech;

import java.util.List;

import com.mipt.tchtech.dto.TaskCreateDto;
import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.dto.TaskUpdateDto;
import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.model.TaskEntity;
import com.mipt.tchtech.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/tasks";
        taskRepository.findAll().forEach(t -> taskRepository.deleteById(t.getId()));
    }

    @Test
    void testGetAllTasks_Positive() {
        taskRepository.save(new TaskEntity("t-1", "Задача 1", "Опис 1", false));
        taskRepository.save(new TaskEntity("t-2", "Задача 2", "Опис 2", true));

        ResponseEntity<List<TaskResponseDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TaskResponseDto>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertNotNull(response.getHeaders().getFirst("X-Total-Count"));
    }

    @Test
    void testGetAllTasks_Negative_Empty() {
        ResponseEntity<List<TaskResponseDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TaskResponseDto>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetTaskById_Positive() {
        taskRepository.save(new TaskEntity("t-1", "Задача 1", "Опис 1", false));

        ResponseEntity<TaskResponseDto> response = restTemplate.getForEntity(baseUrl + "/t-1", TaskResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Задача 1", response.getBody().getTitle());
    }

    @Test
    void testGetTaskById_Negative() {
        ResponseEntity<TaskResponseDto> response = restTemplate.getForEntity(baseUrl + "/non-existent", TaskResponseDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateTask_Positive() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Новая задача");
        dto.setDescription("Описание");
        dto.setPriority(Priority.HIGH);

        ResponseEntity<TaskResponseDto> response = restTemplate.postForEntity(baseUrl, dto, TaskResponseDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Новая задача", response.getBody().getTitle());
        assertTrue(taskRepository.findById(response.getBody().getId()).isPresent());
    }

    @Test
    void testCreateTask_Negative_BlankTitle() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("");
        dto.setPriority(Priority.LOW);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, dto, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateTask_Negative_MissingPriority() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Заголовок");

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, dto, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateTask_Positive() {
        taskRepository.save(new TaskEntity("t-1", "Задача 1", "Опис 1", false));

        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("Задача 1 Обновлена");
        dto.setCompleted(true);

        HttpEntity<TaskUpdateDto> request = new HttpEntity<>(dto);
        ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
                baseUrl + "/t-1", HttpMethod.PUT, request, TaskResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Задача 1 Обновлена", response.getBody().getTitle());
        assertTrue(response.getBody().isCompleted());
    }

    @Test
    void testUpdateTask_Negative() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("Обновление");

        HttpEntity<TaskUpdateDto> request = new HttpEntity<>(dto);
        ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
                baseUrl + "/non-existent", HttpMethod.PUT, request, TaskResponseDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteTask_Positive() {
        taskRepository.save(new TaskEntity("t-1", "Задача 1", "Опис 1", false));

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/t-1", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(taskRepository.findById("t-1").isPresent());
    }

    @Test
    void testDeleteTask_Negative() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/invalid-id", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}

package com.mipt.tchtech;

import com.mipt.tchtech.dto.TaskDto;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

        ResponseEntity<List<TaskDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TaskDto>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAllTasks_Negative_Empty() {
        ResponseEntity<List<TaskDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TaskDto>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetTaskById_Positive() {
        taskRepository.save(new TaskEntity("t-1", "Задача 1", "Опис 1", false));

        ResponseEntity<TaskDto> response = restTemplate.getForEntity(baseUrl + "/t-1", TaskDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Задача 1", response.getBody().getTitle());
    }

    @Test
    void testGetTaskById_Negative() {
        ResponseEntity<TaskDto> response = restTemplate.getForEntity(baseUrl + "/non-existent", TaskDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateTask_Positive() {
        TaskDto newTask = new TaskDto(null, "Новая", "Новое Описание", false);
        ResponseEntity<TaskDto> response = restTemplate.postForEntity(baseUrl, newTask, TaskDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Новая", response.getBody().getTitle());
        
        assertTrue(taskRepository.findById(response.getBody().getId()).isPresent());
    }

    @Test
    void testCreateTask_Negative() {
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, "malformed", String.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
    }

    @Test
    void testUpdateTask_Positive() {
        taskRepository.save(new TaskEntity("t-1", "Задача 1", "Опис 1", false));

        TaskDto updateInfo = new TaskDto(null, "Задача 1 Обновлена", "Опис 1", true);
        HttpEntity<TaskDto> request = new HttpEntity<>(updateInfo);

        ResponseEntity<TaskDto> response = restTemplate.exchange(baseUrl + "/t-1", HttpMethod.PUT, request, TaskDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Задача 1 Обновлена", response.getBody().getTitle());
        assertTrue(response.getBody().isCompleted());
    }

    @Test
    void testUpdateTask_Negative() {
        TaskDto updateInfo = new TaskDto(null, "Задача 1 Обновлена", "Опис 1", true);
        HttpEntity<TaskDto> request = new HttpEntity<>(updateInfo);

        ResponseEntity<TaskDto> response = restTemplate.exchange(baseUrl + "/non-existent", HttpMethod.PUT, request, TaskDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteTask_Positive() {
        taskRepository.save(new TaskEntity("t-1", "Задача 1", "Опис 1", false));

        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/t-1", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(taskRepository.findById("t-1").isPresent());
    }

    @Test
    void testDeleteTask_Negative() {
        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/invalid-id", HttpMethod.DELETE, null, Void.class);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}

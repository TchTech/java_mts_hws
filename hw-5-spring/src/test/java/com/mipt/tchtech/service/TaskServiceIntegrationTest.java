package com.mipt.tchtech.service;

import java.util.Arrays;
import java.util.List;

import com.mipt.tchtech.exception.BulkOperationException;
import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.model.TaskEntity;
import com.mipt.tchtech.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    private Long id1;
    private Long id2;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        TaskEntity t1 = new TaskEntity("Задача 1", "Опис 1", false);
        t1.setPriority(Priority.HIGH);
        TaskEntity t2 = new TaskEntity("Задача 2", "Опис 2", false);
        t2.setPriority(Priority.LOW);
        id1 = taskRepository.save(t1).getId();
        id2 = taskRepository.save(t2).getId();
    }

    @Test
    void bulkComplete_marksAllAsCompleted() {
        taskService.bulkCompleteTasks(Arrays.asList(id1, id2));

        assertTrue(taskRepository.findById(id1).orElseThrow().isCompleted());
        assertTrue(taskRepository.findById(id2).orElseThrow().isCompleted());
    }

    @Test
    void bulkComplete_rollsBackOnMissingId() {
        Long nonExistent = 999_999L;
        List<Long> ids = Arrays.asList(id1, nonExistent);

        assertThrows(BulkOperationException.class, () -> taskService.bulkCompleteTasks(ids));

        assertFalse(taskRepository.findById(id1).orElseThrow().isCompleted(),
                "Транзакция должна откатиться, ни одна задача не должна быть выполнена");
    }

    @Test
    void getAllTasksWithAttachments_works() {
        assertEquals(2, taskService.getAllTasksWithAttachments().size());
    }
}

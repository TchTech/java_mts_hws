package com.mipt.tchtech.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.mipt.tchtech.config.JpaConfig;
import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.model.TaskAttachment;
import com.mipt.tchtech.model.TaskEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager em;

    private TaskEntity newTask(String title, Priority priority, boolean completed, LocalDate due) {
        TaskEntity t = new TaskEntity(title, "desc", completed);
        t.setPriority(priority);
        t.setDueDate(due);
        return t;
    }

    @Test
    void saveAndFind_persistsAuditFields() {
        TaskEntity saved = taskRepository.save(newTask("Audit", Priority.HIGH, false, null));
        em.flush();
        em.clear();

        TaskEntity loaded = taskRepository.findById(saved.getId()).orElseThrow();
        assertNotNull(loaded.getCreatedAt());
        assertEquals("Audit", loaded.getTitle());
    }

    @Test
    void findByCompletedAndPriority_returnsMatching() {
        taskRepository.save(newTask("a", Priority.HIGH, false, null));
        taskRepository.save(newTask("b", Priority.HIGH, true, null));
        taskRepository.save(newTask("c", Priority.LOW, false, null));

        List<TaskEntity> result = taskRepository.findByCompletedAndPriority(false, Priority.HIGH);
        assertEquals(1, result.size());
        assertEquals("a", result.get(0).getTitle());
    }

    @Test
    void findUpcoming_returnsTasksWithin7Days() {
        LocalDate today = LocalDate.now();
        taskRepository.save(newTask("in-3-days", Priority.MEDIUM, false, today.plusDays(3)));
        taskRepository.save(newTask("in-30-days", Priority.MEDIUM, false, today.plusDays(30)));
        taskRepository.save(newTask("no-due", Priority.MEDIUM, false, null));

        List<TaskEntity> upcoming = taskRepository.findUpcoming(today, today.plusDays(7));
        assertEquals(1, upcoming.size());
        assertEquals("in-3-days", upcoming.get(0).getTitle());
    }

    @Test
    void cascadeDelete_removesAttachments() {
        TaskEntity task = newTask("with-att", Priority.MEDIUM, false, null);
        TaskAttachment att = new TaskAttachment();
        att.setFileName("a.txt");
        att.setStoredFileName("stored");
        att.setSize(10L);
        att.setUploadedAt(LocalDateTime.now());
        task.addAttachment(att);

        TaskEntity saved = taskRepository.save(task);
        em.flush();
        Long attId = saved.getAttachments().get(0).getId();
        assertNotNull(attId);

        taskRepository.deleteById(saved.getId());
        em.flush();

        assertFalse(taskRepository.findById(saved.getId()).isPresent());
        assertNull(em.find(TaskAttachment.class, attId));
    }

    private static void assertNull(Object o) {
        assertTrue(o == null);
    }

    @Test
    void findWithAttachmentsById_loadsAttachmentsEagerly() {
        TaskEntity task = newTask("eager", Priority.MEDIUM, false, null);
        Set<String> tags = new HashSet<>();
        tags.add("urgent");
        task.setTags(tags);
        TaskAttachment att = new TaskAttachment();
        att.setFileName("a.txt");
        att.setStoredFileName("stored");
        att.setSize(10L);
        att.setUploadedAt(LocalDateTime.now());
        task.addAttachment(att);

        TaskEntity saved = taskRepository.save(task);
        em.flush();
        em.clear();

        Optional<TaskEntity> loaded = taskRepository.findWithAttachmentsById(saved.getId());
        assertTrue(loaded.isPresent());
        assertEquals(1, loaded.get().getAttachments().size());
    }
}

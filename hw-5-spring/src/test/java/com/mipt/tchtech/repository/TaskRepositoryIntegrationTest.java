package com.mipt.tchtech.repository;

import java.time.LocalDate;
import java.util.List;

import com.mipt.tchtech.config.JpaConfig;
import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.model.TaskEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
class TaskRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("tchtech_test")
            .withUsername("tchtech")
            .withPassword("tchtech");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
    }

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
    void postgresContainerIsRunning() {
        assertThat(postgres.isRunning()).isTrue();
        assertThat(postgres.getJdbcUrl()).startsWith("jdbc:postgresql://");
    }

    @Test
    void findUpcoming_returnsOnlyTasksWithDueDateInRangeOrderedByDueDate() {
        LocalDate today = LocalDate.now();
        taskRepository.save(newTask("через 1 день", Priority.MEDIUM, false, today.plusDays(1)));
        taskRepository.save(newTask("через 5 дней", Priority.HIGH, false, today.plusDays(5)));
        taskRepository.save(newTask("через 30 дней", Priority.LOW, false, today.plusDays(30)));
        taskRepository.save(newTask("без дедлайна", Priority.LOW, false, null));
        em.flush();
        em.clear();

        List<TaskEntity> upcoming = taskRepository.findUpcoming(today, today.plusDays(7));

        assertThat(upcoming).hasSize(2);
        assertThat(upcoming).extracting(TaskEntity::getTitle)
                .containsExactly("через 1 день", "через 5 дней");
        assertThat(upcoming).allMatch(t -> t.getDueDate() != null);
    }
}

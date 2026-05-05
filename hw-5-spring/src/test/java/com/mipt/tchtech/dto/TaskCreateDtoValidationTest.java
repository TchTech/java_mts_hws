package com.mipt.tchtech.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.validation.OnCreate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskCreateDtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        if (factory != null) {
            factory.close();
        }
    }

    private TaskCreateDto validDto() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Корректный заголовок");
        dto.setDescription("Описание");
        dto.setDueDate(LocalDate.now().plusDays(1));
        dto.setPriority(Priority.MEDIUM);
        dto.setTags(new HashSet<>(Set.of("home")));
        return dto;
    }

    @Test
    void validDto_passesOnCreateGroup() {
        Set<ConstraintViolation<TaskCreateDto>> violations =
                validator.validate(validDto(), OnCreate.class);
        assertThat(violations).isEmpty();
    }

    @Test
    void blankTitle_failsOnCreate() {
        TaskCreateDto dto = validDto();
        dto.setTitle("  ");

        Set<ConstraintViolation<TaskCreateDto>> violations =
                validator.validate(dto, OnCreate.class);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("title");
    }

    @Test
    void shortTitle_failsOnCreate() {
        TaskCreateDto dto = validDto();
        dto.setTitle("ab");

        Set<ConstraintViolation<TaskCreateDto>> violations =
                validator.validate(dto, OnCreate.class);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("title");
    }

    @Test
    void nullPriority_failsOnCreate() {
        TaskCreateDto dto = validDto();
        dto.setPriority(null);

        Set<ConstraintViolation<TaskCreateDto>> violations =
                validator.validate(dto, OnCreate.class);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("priority");
    }

    @Test
    void pastDueDate_failsAlwaysActiveValidation() {
        TaskCreateDto dto = validDto();
        dto.setDueDate(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<TaskCreateDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("dueDate");
    }

    @Test
    void tooManyTags_failsAlwaysActiveValidation() {
        TaskCreateDto dto = validDto();
        Set<String> tags = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            tags.add("tag" + i);
        }
        dto.setTags(tags);

        Set<ConstraintViolation<TaskCreateDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("tags");
    }
}

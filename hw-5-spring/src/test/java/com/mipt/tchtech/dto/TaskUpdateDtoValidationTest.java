package com.mipt.tchtech.dto;

import java.time.LocalDate;
import java.util.Set;

import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.validation.OnUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskUpdateDtoValidationTest {

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

    @Test
    void validMinimalUpdate_passesOnUpdateGroup() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setCompleted(true);

        Set<ConstraintViolation<TaskUpdateDto>> violations = validator.validate(dto, OnUpdate.class);
        assertThat(violations).isEmpty();
    }

    @Test
    void shortTitle_failsOnUpdate() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("ab");

        Set<ConstraintViolation<TaskUpdateDto>> violations = validator.validate(dto, OnUpdate.class);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("title");
    }

    @Test
    void dueDateInPast_failsClassLevelValidator() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setDueDate(LocalDate.now().minusDays(5));

        Set<ConstraintViolation<TaskUpdateDto>> violations = validator.validate(dto, OnUpdate.class);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().map(ConstraintViolation::getMessage).toList())
                .anyMatch(m -> m.toLowerCase().contains("past") || m.contains("прошл"));
    }

    @Test
    void tooManyTags_failsAlwaysActiveConstraint() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTags(Set.of("a", "b", "c", "d", "e", "f"));

        Set<ConstraintViolation<TaskUpdateDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("tags");
    }

    @Test
    void validTitleAndDueDate_passesOnUpdate() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("Достаточно длинное");
        dto.setDueDate(LocalDate.now().plusWeeks(1));
        dto.setPriority(Priority.LOW);

        Set<ConstraintViolation<TaskUpdateDto>> violations = validator.validate(dto, OnUpdate.class);
        assertThat(violations).isEmpty();
    }
}

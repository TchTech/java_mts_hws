package com.mipt.tchtech.validation;

import java.time.LocalDate;

import com.mipt.tchtech.dto.TaskUpdateDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DueDateNotBeforeCreationValidatorTest {

    private final DueDateNotBeforeCreationValidator validator = new DueDateNotBeforeCreationValidator();

    @Test
    void nullDto_isValid() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void dtoWithoutDueDate_isValid() {
        TaskUpdateDto dto = new TaskUpdateDto();
        assertThat(validator.isValid(dto, null)).isTrue();
    }

    @Test
    void dueDateInFuture_isValid() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setDueDate(LocalDate.now().plusDays(1));
        assertThat(validator.isValid(dto, null)).isTrue();
    }

    @Test
    void dueDateToday_isValid() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setDueDate(LocalDate.now());
        assertThat(validator.isValid(dto, null)).isTrue();
    }

    @Test
    void dueDateInPast_isInvalid() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setDueDate(LocalDate.now().minusDays(1));
        assertThat(validator.isValid(dto, null)).isFalse();
    }
}

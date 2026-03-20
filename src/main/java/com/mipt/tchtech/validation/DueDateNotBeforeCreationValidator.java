package com.mipt.tchtech.validation;

import java.time.LocalDate;

import com.mipt.tchtech.dto.TaskUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DueDateNotBeforeCreationValidator
        implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

    @Override
    public boolean isValid(TaskUpdateDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getDueDate() == null) {
            return true;
        }
        return !dto.getDueDate().isBefore(LocalDate.now());
    }
}

package com.mipt.angelikaliber.validation;

import com.mipt.angelikaliber.dto.TaskUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DueDateNotBeforeCreationValidator
        implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

    @Override
    public boolean isValid(TaskUpdateDto value, ConstraintValidatorContext context) {
        if (value == null || value.getDueDate() == null) {
            return true;
        }
        return !value.getDueDate().isBefore(LocalDate.now());
    }
}

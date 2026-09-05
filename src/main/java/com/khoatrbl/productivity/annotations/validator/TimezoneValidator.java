package com.khoatrbl.productivity.annotations.validator;

import com.khoatrbl.productivity.annotations.ValidTimezone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.ZoneId;

public class TimezoneValidator implements ConstraintValidator<ValidTimezone, String> {

    @Override
    public boolean isValid(String timezone, ConstraintValidatorContext constraintValidatorContext) {

        if (timezone == null || timezone.isBlank()) {
            return false;
        }

        try {
            ZoneId.of(timezone);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}

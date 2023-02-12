package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateValidator implements ConstraintValidator<IsAfter, LocalDate> {
    LocalDate validDate;

    @Override
    public void initialize(IsAfter constraintAnnotation) {
        validDate = LocalDate.parse(constraintAnnotation.value(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        return date.isAfter(validDate);
    }
}
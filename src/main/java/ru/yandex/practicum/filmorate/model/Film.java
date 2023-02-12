package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.IsAfter;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private static int currentId = 0;
    private int id;
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    @IsAfter("1895-12-27")
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
}

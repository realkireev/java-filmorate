package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.IsAfter;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Collection;

@Data
@AllArgsConstructor
public class Film {
    private Integer id;
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    @IsAfter("1895-12-27")
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    private Mpa mpa;
    private Collection<Genre> genres;
    @JsonIgnore
    private Collection<Integer> userIdLikes;
}

package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Film {
    private static int currentId = 0;
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
}

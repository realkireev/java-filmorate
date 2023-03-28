package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private Integer id;
    @NotNull
    @Email
    private final String email;
    @NotBlank
    @Pattern(regexp = "^\\S+$", message = "Whitespaces are not allowed in login")
    private final String login;
    private String name;
    @PastOrPresent
    private final LocalDate birthday;
}

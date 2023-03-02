package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class User {

    int id;

    @Email
    String email;

    @NotNull
    @NotBlank
    String login;
    @Size
    String name;

    @NotNull
    LocalDate birthday;
}

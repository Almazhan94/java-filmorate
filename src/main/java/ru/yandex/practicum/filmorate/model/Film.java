package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    int id;

    Set<Integer> likes = new HashSet<>();

    @NotNull
    @NotBlank
    String name;

    @Size(max = 200)
    String description;

    LocalDate releaseDate;

    @Positive
    long duration;

    public void addLike(int userId) {
        likes.add(userId);
    }
}

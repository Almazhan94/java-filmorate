package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
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

    Mpa mpa;

    Set<Genre> genres = new HashSet<>();

    public void addLike(int userId) {
        likes.add(userId);
    }
    public void addGenre(List<Genre> genreList) {
        genres = new HashSet<>(genreList);
    }
    public void deleteGenre(Genre genre) {
        genres.remove(genre);
    }

}

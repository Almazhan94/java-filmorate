package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UpdateExсeption;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    public final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 0;

    @GetMapping
    public List<Film> findAll() {
        log.info("количество добавленных фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validator(film);
        film.setId(++filmId);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        validator(film);
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
        } else {
            throw new UpdateExсeption("Фильм с Id= " + film.getId() + " не существует");
        }
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    private void validator(Film film) {
        LocalDate releaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(releaseDate)) {
            throw new ValidationException("Не корректная дата релиза (например, дата релиза не может быть раньше 28 декабря 1895 года)");
        }
    }
}

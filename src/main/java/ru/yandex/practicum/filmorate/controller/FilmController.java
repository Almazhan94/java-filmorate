package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UpdateExсeption;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

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
        //log.debug("количество пользователей в текущий момент: {}", users.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (validator(film)) {
            film.setId(++filmId);
            films.put(film.getId(), film);
        }
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (validator(film)) {
            int id = film.getId();
            if (films.containsKey(id)) {
                films.put(id, film);
            } else {
                throw new UpdateExсeption("Фильм с Id= " + film.getId() + " не найден в HashMap");
            }
        }
        log.debug("Обновлен фильм: {}", film);
        return film;
    }

    private boolean validator(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Не корректное название фильма (например, название не может быть пустым)");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Не корректное описание фильма (например, максимальная длина описания — 200 символов)");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Не корректная продолжительность (например, продолжительность фильма должна быть положительной)");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Не корректная дата релиза (например, дата релиза не может быть раньше 28 декабря 1895 года)");
        }
        return true;
    }
}

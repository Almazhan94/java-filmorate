package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final FilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("количество добавленных фильмов: {}", inMemoryFilmStorage.findAll().size());
        return inMemoryFilmStorage.findAll();
    }

    @GetMapping("/{filmId}")
    public Film findFilmById(@PathVariable int filmId) {
        log.info("Найден фильм: {}", inMemoryFilmStorage.findFilmById(filmId));
        return inMemoryFilmStorage.findFilmById(filmId);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавлен фильм: {}", film);
        return inMemoryFilmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновлен фильм: {}", film);
        return inMemoryFilmStorage.update(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("пользователь поставил лайк фильму: {}", inMemoryFilmStorage.findFilmById(filmId));
        filmService.addLike(filmId, userId);
        return inMemoryFilmStorage.findFilmById(filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteFriend(@PathVariable int filmId, @PathVariable int userId) {
        log.info("пользователь удалил удалил лайк фильму: {}", inMemoryFilmStorage.findFilmById(filmId));
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", required = false, defaultValue = "10") int count) {
        log.info("Список самых популярных фильмов: {}", filmService.getPopularFilms(count));
        return filmService.getPopularFilms(count);
    }
}

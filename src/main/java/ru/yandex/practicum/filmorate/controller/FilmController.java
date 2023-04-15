package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final FilmDbStorage filmDbStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmDbStorage filmDbStorage, FilmService filmService) {
        this.filmDbStorage = filmDbStorage;
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        log.info("Количество добавленных фильмов: {}", filmDbStorage.findAll().size());
        return filmDbStorage.findAll();
    }

    @GetMapping("/films/{filmId}")
    public Film findFilmById(@PathVariable int filmId) {
        log.info("Ищется фильм: {}", filmDbStorage.findFilmById(filmId));
        return filmDbStorage.findFilmById(filmId);
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавляется фильм: {}", film);
        return filmDbStorage.create(film);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновляется фильм: {}", film);
        return filmDbStorage.update(film);
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public Film addLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("Пользователь поставил лайк фильму: {}", filmDbStorage.findFilmById(filmId));
        return filmService.addLike(filmId, userId);
    }
      @DeleteMapping("/films/{filmId}/like/{userId}")
      public Film deleteFriend(@PathVariable int filmId, @PathVariable int userId) {
      log.info("Пользователь удалил лайк фильму: {}", filmDbStorage.findFilmById(filmId));
      return filmService.deleteLike(filmId, userId);
      }

      @GetMapping("/films/popular")
      public List<Film> getPopularFilms(@RequestParam(value = "count", required = false, defaultValue = "10") int count) {
      log.info("Список самых популярных фильмов: {}", filmService.getPopularFilms(count));
      return filmService.getPopularFilms(count);
      }

    @GetMapping("/mpa")
    public List<Mpa> getMpa() {
        log.info("Ищется таблица рейтингов MPA: {}", filmDbStorage.getMpa().size());
        return filmDbStorage.getMpa();
    }

    @GetMapping("/mpa/{mpaId}")
    public Mpa findMpaById(@PathVariable int mpaId) {
        log.info("Ищется рейтинг MPA: {}", filmDbStorage.getMpaById(mpaId));
        return filmDbStorage.getMpaById(mpaId);
    }

    @GetMapping("/genres")
    public List<Genre> getGenre() {
        log.info("Ищется таблица жанров: {}", filmDbStorage.getGenre().size());
        return filmDbStorage.getGenre();
    }

    @GetMapping("/genres/{genreId}")
    public Genre findGenreById(@PathVariable int genreId) {
        log.info("Ищется жанр: {}", filmDbStorage.getGenreById(genreId));
        return filmDbStorage.getGenreById(genreId);
    }

}

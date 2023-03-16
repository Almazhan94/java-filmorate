package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UpdateExсeption;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage{

    public final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 0;
    private static final LocalDate releaseDate = LocalDate.of(1895, 12, 28);

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Film findFilmById(int filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %s не существует.",filmId));
        }
    }

    @Override
    public Film create(Film film) {
        validator(film);
        if (films.containsKey(film.getId())) {
            throw new FilmAlreadyExistException(String.format("Фильм с идентификатором %s уже зарегистрирован.", film.getId()));
        }
        film.setId(++filmId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        validator(film);
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
            return film;
        } else {
            throw new UpdateExсeption(String.format("Фильм с идентификатором %s не существует.",film.getId()));
        }
    }

    private void validator(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(releaseDate)) {
            throw new ValidationException("Не корректная дата релиза (например, дата релиза не может быть раньше " +
                    "28 декабря 1895 года)");
        }
    }
}

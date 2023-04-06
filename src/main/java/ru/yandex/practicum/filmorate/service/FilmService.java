package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    InMemoryFilmStorage inMemoryFilmStorage;
    InMemoryUserStorage inMemoryUserStorage;

    static Comparator<Film> comparator = new Comparator<Film>() {
        @Override
        public int compare(Film f1, Film f2) {
            int firstLike = f1.getLikes().size();
            int secondLike = f2.getLikes().size();
            if (firstLike == secondLike) {
                return f1.getId() - f2.getId();
            } else {
                return secondLike - firstLike;
            }
        }
    };

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Film addLike(int filmId, int userId) {
        Film film = inMemoryFilmStorage.findFilmById(filmId);
        User user = inMemoryUserStorage.findUserById(userId);
        film.addLike(userId);
        return film;
    }

    public Film deleteLike(int filmId, int userId) {
        Film film = inMemoryFilmStorage.findFilmById(filmId);
        User user = inMemoryUserStorage.findUserById(userId);
        if (film.getLikes().remove(userId)) {
            return film;
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден в списке лайков у фильма %d", userId, filmId));
        }
    }

    public List<Film> getPopularFilms(int count) {
        return inMemoryFilmStorage.findAll().stream()
                .sorted(comparator)
                .limit(count)
                .collect(Collectors.toList());
    }
}

package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

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

    public Film addLike(int filmId, int userId){
        if (inMemoryFilmStorage.films.containsKey(filmId) && inMemoryUserStorage.users.containsKey(userId)) {
            inMemoryFilmStorage.films.get(filmId).addLike(userId);
            return inMemoryFilmStorage.films.get(filmId);
        } else {
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    public Film deleteLike(int filmId, int userId){
        if (inMemoryFilmStorage.films.containsKey(filmId)
                && inMemoryUserStorage.users.containsKey(userId)
                && inMemoryFilmStorage.films.get(filmId).getLikes().contains(userId)) {
            inMemoryFilmStorage.films.get(filmId).getLikes().remove(userId);
            return inMemoryFilmStorage.films.get(filmId);
        } else {
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    public List<Film> getPopularFilms(int count){
        int filmsHashMapSize = inMemoryFilmStorage.films.size();
        List<Film> films = new ArrayList<>(inMemoryFilmStorage.films.values());
        Set<Film> filmSet = new TreeSet<>(comparator);
        if (count == 10) {
            if (filmsHashMapSize <= 10) {
                filmSet.addAll(films);
                return filmSet.stream().toList();
            } else {
                filmSet.addAll(films);
                return filmSet.stream().limit(count).toList();
            }
        }
           else if (count > filmsHashMapSize) {
            filmSet.addAll(films);
            return filmSet.stream().limit(filmsHashMapSize).toList();
        } else if (count <= filmsHashMapSize) {
            filmSet.addAll(films);
        }
        return filmSet.stream().limit(count).toList();
    }
}

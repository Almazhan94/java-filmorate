package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import javax.management.RuntimeErrorException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    FilmDbStorage filmDbStorage;
    UserDbStorage userDbStorage;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage, UserDbStorage userDbStorage, JdbcTemplate jdbcTemplate) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Film addLike(int filmId, int userId) {
        Film film = filmDbStorage.findFilmById(filmId);
        User user = userDbStorage.findUserById(userId);
        String sqlUserId = "select user_id from film_like where film_id = ?";
        List<Integer> idList = jdbcTemplate.query(sqlUserId, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt("user_id");
            }
        }, filmId);

        if (idList.size() != 1) {
            String sql = "insert into film_like (film_id, user_id) values (?, ?)";
            jdbcTemplate.update(sql, film.getId(), user.getId());
            return film;
        } else {
            throw new RuntimeException(String.format("Пользователь с идентификатором %d уже в списке лайков у фильма %d", userId, filmId));
        }
    }

    public Film deleteLike(int filmId, int userId) {
        Film film = filmDbStorage.findFilmById(filmId);
        User user = userDbStorage.findUserById(userId);
        String sqlDelete = "DELETE FROM FILM_LIKE WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlDelete, film.getId(), user.getId());
        return film;
        }

    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT  film_id, " +
                "name, " +
                "description, " +
                "release_date, " +
                "duration " +
                "FROM film " +
                "WHERE film_id IN (SELECT FILM_ID " +
                                    "FROM FILM_LIKE fl " +
                                     "WHERE FILM_ID " +
                                     "GROUP BY FILM_ID " +
                                     "ORDER BY count(*) DESC) " +
                                     "LIMIT ?";
        List<Film> popularFilm = jdbcTemplate.query(sql, new RowMapper<Film>() {
            @Override
            public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                Film film = Film.builder()
                        .id(rs.getInt("film_id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getLong("duration"))
                        .build();
                return film;
            }
        }, count);
        if (popularFilm.size() == 0) {
            return filmDbStorage.findAll();
        }
        return popularFilm;
    }
}

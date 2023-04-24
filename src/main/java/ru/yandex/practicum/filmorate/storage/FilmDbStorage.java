package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
public class FilmDbStorage implements FilmStorage {

    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    private static final LocalDate releaseDate = LocalDate.of(1895, 12, 28);

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select * from film";
        return jdbcTemplate.query(sqlQuery, filmRowMapper());
    }

    @Override
    public Film create(Film film) {
        validator(film);
        String sql = "insert into film (name, description, release_date, duration, mpa_id) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"film_id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            preparedStatement.setInt(5, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        updateFilmGenreInDB(film);
        return film;

    }

    @Override
    public Film update(Film film) {
        validator(film);
        List<Film> filmList = jdbcTemplate.query("select * from film where film_id = ?", filmRowMapper(), film.getId());
        if (filmList.size() != 1) {
            throw new UpdateException(String.format("Фильм с идентификатором %d не существует.", film.getId()));
        }
        String sqlQuery = "update film set name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? where film_id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        updateFilmGenreInDB(film);
        List<Genre> genreList = getFilmGenre(film);
        film.addGenre(genreList);
        return film;
    }

    @Override
    public Film findFilmById(int filmId) {
        String sql = "select * from film where film_id = ?";
        List<Film> filmList = jdbcTemplate.query(sql, filmRowMapper(), filmId);
        if (filmList.size() != 1) {
            throw new UpdateException(String.format("Фильм с идентификатором %d не существует.", filmId));
        }
        Film film = jdbcTemplate.queryForObject(sql, filmRowMapper(), filmId);

        return film;
    }

    private void validator(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(releaseDate)) {
            throw new ValidationException("Не корректная дата релиза (например, дата релиза не может быть раньше " +
                    "28 декабря 1895 года)");
        }
    }

    private RowMapper<Film> filmRowMapper() {
        return new RowMapper<Film>() {
            @Override
            public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                Film film = Film.builder()
                        .id(rs.getInt("film_id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getLong("duration"))
                        .build();
                film.setMpa(getFilmMpa(film));
                List<Genre> genreList = getFilmGenre(film);
                film.addGenre(genreList);
                return film;
            }
        };
    }

    private void updateFilmGenreInDB(Film film) {
        if (film.getGenres() == null) {
            return;
        }
            String sqlDelete = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
            jdbcTemplate.update(sqlDelete, film.getId());

        for (Genre genre : film.getGenres()) {
            String sql = "insert into film_genre (film_id, genre_id) values (?, ?)";
            jdbcTemplate.update(sql,
                    film.getId(),
                    genre.getId()
            );
        }
    }

    private Mpa getFilmMpa(Film film) {
        String sqlMpaId = "select mpa_id from film where film_id = ?";
        int mpaId = jdbcTemplate.queryForObject(sqlMpaId, Integer.class, film.getId());
        String sql = "select * from mpa where mpa_id = ?";
        Mpa mpa = jdbcTemplate.queryForObject(sql, new RowMapper<Mpa>() {
            @Override
            public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("name"))
                        .build();
            }
        }, mpaId);
        return mpa;
    }

    private List<Genre> getFilmGenre(Film film) {
        String sql = "SELECT g.GENRE_ID, g.NAME FROM genre AS g INNER JOIN FILM_GENRE fg ON g.GENRE_ID = fg.GENRE_ID " +
                " WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Genre.builder()
                        .id(rs.getInt("genre_id"))
                        .name(rs.getString("name"))
                        .build();
            }
        }, film.getId());
        genres.sort(Comparator.comparing(Genre::getId));
        return genres;
    }

    public List<Mpa> getMpa() {
        String sql = "select * from mpa";
        List<Mpa> mpaList = jdbcTemplate.query(sql, new RowMapper<Mpa>() {
            @Override
            public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("name"))
                        .build();
            }
        });
        return mpaList;
    }

    public Mpa getMpaById(int mpaId) {

        String sql = "select * from mpa where mpa_id = ?";
        List<Mpa> mpaList  = jdbcTemplate.query(sql, new RowMapper<Mpa>() {
            @Override
            public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("name"))
                        .build();
            }
        }, mpaId);
        if (mpaList.size() != 1) {
            throw new MpaNotFoundException(String.format("Рейтинг MPA с идентификатором %d не существует", mpaId));
        }
        return mpaList.get(0);
    }

    public List<Genre> getGenre() {
        String sql = "select * from genre";
        List<Genre> genreList = jdbcTemplate.query(sql, new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Genre.builder()
                        .id(rs.getInt("genre_id"))
                        .name(rs.getString("name"))
                        .build();
            }
        });
        return genreList;
    }

    public Genre getGenreById(int genreId) {
        String sql = "select * from genre where genre_id = ?";
        List<Genre> genreList = jdbcTemplate.query(sql, new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Genre.builder()
                        .id(rs.getInt("genre_id"))
                        .name(rs.getString("name"))
                        .build();
            }
        }, genreId);
        if (genreList.size() != 1) {
            throw new GenreNotFoundException(String.format("Жанр с идентификатором %d не существует", genreId));
        }
        return genreList.get(0);
    }

}

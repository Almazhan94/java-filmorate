package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmIntegrationTest {

    private final FilmDbStorage filmStorage;
    Film film;

    @BeforeEach
    void setUp() {
        film =  Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1980, 8, 20))
                .duration(100)
                .mpa(Mpa.builder().id(1).name("P").build())
                .build();

        filmStorage.create(film);
    }

    @Test
    public void testFindFilmById() {

        Optional<Film> filmOptional = Optional.of(filmStorage.findFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film1 -> assertThat(film1).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindAllFilm() {
        List<Film> filmList = filmStorage.findAll();
        assertThat(filmList)
                .isNotEmpty()
                .containsOnlyOnce(filmList.get(0));
    }

    @Test
    public void testCreateFilm() {
        Film createFilm =  Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1980, 8, 20))
                .duration(100)
                .mpa(Mpa.builder().id(1).name("P").build())
                .build();

        filmStorage.create(createFilm);

        Optional<Film> filmOptional = Optional.of(createFilm);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film1 -> assertThat(film1).hasFieldOrPropertyWithValue("id", 7)
                );
    }

    @Test
    public void testUpdateUser() {
        film.setName("nisi eiusmodUpdate");
        Film createFilm = filmStorage.update(film);

        Optional<Film> userOptional = Optional.of(createFilm);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(film1 -> assertThat(film1).hasFieldOrPropertyWithValue("name", "nisi eiusmodUpdate")
                );
    }

    @Test
    public void testFindAllMpa() {

        List<Mpa> mpaList = filmStorage.getMpa();
        assertThat(mpaList)
                .isNotEmpty()
                .containsOnlyOnce(mpaList.get(0));
        System.out.println(mpaList.get(0));
    }

    @Test
    public void testFindMpaById() {

        Optional<Mpa> mpaById = Optional.of(filmStorage.getMpaById(1));

        assertThat(mpaById)
                .isPresent()
                .hasValueSatisfying(mpa -> assertThat(mpa).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindAllGenres() {

        List<Genre> genreList = filmStorage.getGenre();
        assertThat(genreList)
                .isNotEmpty()
                .containsOnlyOnce(genreList.get(0));
        System.out.println(genreList.get(0));
    }

    @Test
    public void testFindGenreById() {

        Optional<Genre> genreById = Optional.of(filmStorage.getGenreById(1));

        assertThat(genreById)
                .isPresent()
                .hasValueSatisfying(genre -> assertThat(genre).hasFieldOrPropertyWithValue("id", 1)
                );
    }

}

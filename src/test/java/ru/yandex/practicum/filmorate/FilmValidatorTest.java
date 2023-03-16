/*
package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidatorTest {
    FilmController filmController;
    Film film;

    @BeforeEach
    void setUp() {
        filmController = new FilmController(new InMemoryFilmStorage());
        film = new Film();
        film.setId(1);
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1980, 8, 20));
        film.setDuration(100);
    }

    @Test
    public void shouldReturnExceptionThenReleaseDateIncorrect() {

        film.setReleaseDate(null);
        Throwable thrownBlank = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertNotNull(thrownBlank.getMessage());
        assertFalse(filmController.films.containsValue(film));

        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        Throwable thrownNull = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertNotNull(thrownNull.getMessage());
        assertFalse(filmController.films.containsValue(film));
    }
}
*/

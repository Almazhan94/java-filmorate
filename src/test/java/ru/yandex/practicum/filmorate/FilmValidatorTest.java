package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidatorTest {

    FilmController filmController;

    Film film;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        film = new Film();
        film.setId(1);
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1980, 8, 20));
        film.setDuration(100);
    }

    @Test
    public void shouldReturnExceptionThenNameNullOrBlank() {

        film.setName("");
        Throwable thrownBlank = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertNotNull(thrownBlank.getMessage());
        assertFalse(filmController.films.containsValue(film));

        film.setName(null);
        Throwable thrownNull = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertNotNull(thrownNull.getMessage());
        assertFalse(filmController.films.containsValue(film));
    }

    @Test
    public void shouldReturnExceptionThenDescriptionMoreThan200() {

        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать " +
                "господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время" +
                " «своего отсутствия», стал кандидатом Коломбани.");

        Throwable thrownBlank = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertNotNull(thrownBlank.getMessage());
        assertFalse(filmController.films.containsValue(film));
    }

    @Test
    public void shouldReturnExceptionThenDurationLessThan0() {

        film.setDuration(-5);
        Throwable thrownBlank = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertNotNull(thrownBlank.getMessage());
        assertFalse(filmController.films.containsValue(film));
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

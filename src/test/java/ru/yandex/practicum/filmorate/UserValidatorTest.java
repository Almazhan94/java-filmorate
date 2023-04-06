package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {
    UserController userController;
    User user;
    InMemoryUserStorage inMemoryUserStorage;

    @BeforeEach
    void setUp() {

        inMemoryUserStorage = new InMemoryUserStorage();
        userController = new UserController(inMemoryUserStorage, new UserService(inMemoryUserStorage));
         user = new User();
        user.setId(1);
        user.setEmail("yandex@mail.ru");
        user.setLogin("dolore");
        user.setName("dolore ullamco");
        user.setBirthday(LocalDate.of(1980, 8, 20));
    }

    @Test
    public void shouldReturnExceptionThenLoginIncorrect() {

        user.setLogin("abc defg");
        Throwable thrownAt = assertThrows(ValidationException.class, () -> userController.create(user));
        assertNotNull(thrownAt.getMessage());
        assertFalse(inMemoryUserStorage.users.containsValue(user));
    }

    @Test
    public void shouldReturnExceptionThenBirthdayIncorrect() {

        user.setBirthday(LocalDate.MAX);
        Throwable thrownAt = assertThrows(ValidationException.class, () -> userController.create(user));
        assertNotNull(thrownAt.getMessage());
        assertFalse(inMemoryUserStorage.users.containsValue(user));
    }

    @Test
    public void shouldCreateUserThenNameNullOrBlank() {

        user.setName(null);
         userController.create(user);
        assertNotNull(user.getName());
        assertTrue(inMemoryUserStorage.users.containsValue(user));
        assertEquals(user.getName(), user.getLogin());

        user.setId(2);
        user.setName("");
        userController.create(user);
        assertNotNull(user.getName());
        assertTrue(inMemoryUserStorage.users.containsValue(user));
        assertEquals(user.getName(), user.getLogin());
    }
}

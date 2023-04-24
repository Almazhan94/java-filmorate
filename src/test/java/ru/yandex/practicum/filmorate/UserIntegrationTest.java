package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserIntegrationTest {
    private final UserDbStorage userStorage;
    User user = User.builder()
           .name("dolore ullamco")
           .email("yandex@mail.ru")
           .login("dolore")
           .birthday(LocalDate.of(1980, 8, 20))
           .build();

    @BeforeEach
    void setUp() {
        userStorage.create(user);
    }

    @Test
    public void testFindUserById() {
        User userFind = userStorage.findUserById(1);

        Optional<User> userOptional = Optional.of(userFind);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindAllUser() {
        User userFind = userStorage.findUserById(1);
        List<User> userList = userStorage.findAll();

        assertThat(userList)
                .isNotEmpty()
                .containsOnlyOnce(userList.get(0));
    }

    @Test
    public void testCreateUser() {
        User userFirst = User.builder()
                .name("dolore ullamco")
                .email("yandex@mail.ru")
                .login("dolore")
                .birthday(LocalDate.of(1980, 8, 20))
                .build();
        User createUser = userStorage.create(userFirst);

        Optional<User> userOptional = Optional.of(createUser);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userTest -> assertThat(userTest).hasFieldOrPropertyWithValue("id", 4)
                );
    }

    @Test
    public void testUpdateUser() {
        user.setLogin("doloreUpdate");
        User createUser = userStorage.update(user);

        Optional<User> userOptional = Optional.of(createUser);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userTest -> assertThat(userTest).hasFieldOrPropertyWithValue("login", "doloreUpdate")
                );
    }
}

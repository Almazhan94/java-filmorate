package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Component
public class InMemoryUserStorage implements UserStorage{
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private int userId = 0;
    public HashMap<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User findUserById(int userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
    }

    @Override
    public User create(User user) {
        validator(user);
        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistException(String.format("Пользователь с электронной почтой %s уже зарегистрирован.",
                    user.getEmail()));
        }
        user.setId(++userId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        validator(user);
        int id = user.getId();
        if (users.containsKey(id)) {
            users.put(id, user);
        } else {
            throw new UpdateException(String.format("Пользователь с идентификатором %d не существует", id));
        }
        return user;
    }

    private void validator(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Не корректный логин (например, равен null или пустой строке)");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.warn("Для отображения имени будет использован логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Не корректная дата рождения (например, дата рождения не может быть в будущем)");
        }
    }
}

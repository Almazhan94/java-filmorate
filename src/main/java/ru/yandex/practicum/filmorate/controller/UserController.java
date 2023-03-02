package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private int userId = 0;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        log.info("количество пользователей в текущий момент: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validator(user);
        user.setId(++userId);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        validator(user);
        int id = user.getId();
        if (users.containsKey(id)) {
            users.put(id, user);
        }
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    public void validator(User user) {
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

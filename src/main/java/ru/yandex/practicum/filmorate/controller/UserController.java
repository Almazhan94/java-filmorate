package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    UserDbStorage userDbStorage;
    UserService userService;

    @Autowired
    public UserController( UserDbStorage userDbStorage, UserService userService) {
        this.userDbStorage = userDbStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Количество пользователей в текущий момент: {}", userDbStorage.findAll().size());
        return userDbStorage.findAll();
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable int userId) {
        log.info("Ищется пользователь: {}", userDbStorage.findUserById(userId));
        return userDbStorage.findUserById(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Добавляется пользователь: {}", user);
        return userDbStorage.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        log.info("Обновляется пользователь: {}", user);
        return userDbStorage.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info(String.format("Пользователь с идентификатором %d добавляется в друзья к %d", friendId, id));
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info(String.format("Пользователь с идентификатором %d удаляется из друзей у %d", friendId, id));
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriend(@PathVariable int id) {
        log.info("Список друзей пользователя: {}", userService.getFriends(id));
        return userService.getFriends(id);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriend(@PathVariable int userId, @PathVariable int otherId) {
        log.info("Список общих друзей: {}", userService.getCommonFriends(userId, otherId));
        return userService.getCommonFriends(userId, otherId);
    }
}
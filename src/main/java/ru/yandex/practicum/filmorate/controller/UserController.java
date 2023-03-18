package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    InMemoryUserStorage inMemoryUserStorage;
    UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Количество пользователей в текущий момент: {}", inMemoryUserStorage.findAll().size());
        return inMemoryUserStorage.findAll();
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable int userId) {
        log.info("Ищется пользователь: {}", inMemoryUserStorage.findUserById(userId));
        return inMemoryUserStorage.findUserById(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Добавляется пользователь: {}", user);
        return inMemoryUserStorage.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        log.info("Обновляется пользователь: {}", user);
        return inMemoryUserStorage.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Пользователь добавлен в друзья: {}", inMemoryUserStorage.findUserById(id));
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Пользователь удалил из друзей: {}", inMemoryUserStorage.findUserById(friendId));
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriend(@PathVariable int id) {
        log.info("Список друзей пользователя: {}", userService.getFriends(id));
        return userService.getFriends(id);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getСommonFriend(@PathVariable int userId, @PathVariable int otherId) {
        log.info("Список общих друзей: {}", userService.getFriends(userId));
        return userService.getСommonFriends(userId, otherId);
    }
}
package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addFriend(int userId, int friendId) {
        if (inMemoryUserStorage.users.containsKey(userId)) {
            if (inMemoryUserStorage.users.containsKey(friendId)) {
                inMemoryUserStorage.users.get(userId).addFriend(friendId);
                return inMemoryUserStorage.users.get(friendId);
            } else {
                throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден", friendId));
            }
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }
    }

    public User deleteFriend(int userId, int friendId) {
        if (inMemoryUserStorage.users.containsKey(userId)) {
            if (inMemoryUserStorage.users.containsKey(friendId)) {
                if (inMemoryUserStorage.users.get(userId).getFriends().contains(friendId)) {
                    inMemoryUserStorage.users.get(userId).getFriends().remove(friendId);
                    return inMemoryUserStorage.users.get(friendId);
                } else {
                    throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден в списке друзей", friendId));
                }
            } else {
                throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден", friendId));
            }
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }
    }

    public List<User> getFriends(int userId) {
        List<User> friends = new ArrayList<>();
        if (inMemoryUserStorage.users.containsKey(userId)) {
            for (Integer friendId : inMemoryUserStorage.users.get(userId).getFriends()) {
                friends.add(inMemoryUserStorage.users.get(friendId));
            }
            return friends;
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }
    }

    public List<User> getСommonFriends(int userId, int otherId) {
        List<User> commonFriends;
        Set<Integer> userFriends = inMemoryUserStorage.users.get(userId).getFriends();
        userFriends.retainAll(inMemoryUserStorage.users.get(otherId).getFriends());
        commonFriends = userFriends.stream()
                .map(id -> inMemoryUserStorage.users.get(id))
                .collect(Collectors.toList());
        return commonFriends;
    }

}

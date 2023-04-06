package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;


import java.util.ArrayList;
import java.util.HashSet;
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

        User user = inMemoryUserStorage.findUserById(userId);
        User friend = inMemoryUserStorage.findUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        return friend;
    }

    public User deleteFriend(int userId, int friendId) {
        User user = inMemoryUserStorage.findUserById(userId);
        User friend = inMemoryUserStorage.findUserById(friendId);
        if (user.getFriends().remove(friendId) && friend.getFriends().remove(userId)) {
            return friend;
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден в списке друзей у %d", friendId, userId));
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
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден", userId));
        }
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        List<User> commonFriends;

        Set<Integer> userFriends = new HashSet<>(inMemoryUserStorage.users.get(userId).getFriends());
        Set<Integer> otherUserFriends = new HashSet<>(inMemoryUserStorage.users.get(otherId).getFriends());
        userFriends.retainAll(otherUserFriends);
        commonFriends = userFriends.stream()
                .map(id -> inMemoryUserStorage.users.get(id))
                .collect(Collectors.toList());
        return commonFriends;
    }

}

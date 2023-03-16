package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addFriend(int userId, int friendId) {

        if (inMemoryUserStorage.users.containsKey(userId) && inMemoryUserStorage.users.containsKey(friendId)) {
            inMemoryUserStorage.users.get(userId).addFriend(friendId);
            return inMemoryUserStorage.users.get(friendId);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public User deleteFriend(int userId, int friendId) {
        if (inMemoryUserStorage.users.containsKey(userId)
                && inMemoryUserStorage.users.containsKey(friendId)
                && inMemoryUserStorage.users.get(userId).getFriends().contains(friendId)) {
            inMemoryUserStorage.users.get(userId).getFriends().remove(friendId);
            return inMemoryUserStorage.users.get(friendId);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public List<User> getFriends(int userId) {
        List<User> friends = new ArrayList<>();
        if (inMemoryUserStorage.users.containsKey(userId)) {
            for(Integer friendId : inMemoryUserStorage.users.get(userId).getFriends()) {
                friends.add(inMemoryUserStorage.users.get(friendId));
            }
            return friends;
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public List<User> getСommonFriends(int userId, int otherId) {
        List<User> commonFriends = new ArrayList<>();
        Set<Integer> userFriends = inMemoryUserStorage.users.get(userId).getFriends();
        Set<Integer> otherFriends = inMemoryUserStorage.users.get(otherId).getFriends();
        if (inMemoryUserStorage.users.containsKey(userId) && inMemoryUserStorage.users.containsKey(otherId)) {

            for (int i = 0; i < userFriends.size(); i++) {

                int a = userFriends.stream().toList().get(i);

                for (int j = 0; j < otherFriends.size(); j++) {

                    if (a == otherFriends.stream().toList().get(j)) {

                        commonFriends.add(inMemoryUserStorage.users.get(a));
                    }
                }
            }
            return commonFriends;
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

}

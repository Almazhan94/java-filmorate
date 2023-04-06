package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    int id;

    Set<Integer> friends = new HashSet<>();

    @Email
    String email;

    @NotNull
    @NotBlank
    String login;
    @Size
    String name;

    @NotNull
    LocalDate birthday;

    public void addFriend(int friendId) {
        friends.add(friendId);
    }
}

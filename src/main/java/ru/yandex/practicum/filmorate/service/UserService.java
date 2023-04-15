package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class UserService {
    UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(UserDbStorage userDbStorage, JdbcTemplate jdbcTemplate) {
        this.userDbStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public User addFriend(int userId, int friendId) {

        User user = userDbStorage.findUserById(userId);
        User friend = userDbStorage.findUserById(friendId);

        String sql = "select count(*) from user_friend where user_id = ? and friend_id = ?";
        List<Integer> intList = jdbcTemplate.query(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        },userId, friendId);
        if (intList.get(0) == 1) {
            throw new UserAlreadyExistException(String.format("Пользователь с идентификатором %d уже добавлен в друзья к %d", friendId, userId));
        }
            String sqlInsert = "insert into user_friend(user_id, friend_id) values (?, ?)";
            jdbcTemplate.update(sqlInsert, userId, friendId);
            return user;
    }

    public User deleteFriend(int userId, int friendId) {
        User user = userDbStorage.findUserById(userId);
        User friend = userDbStorage.findUserById(friendId);
        String sql = "select count(*) from user_friend where user_id = ? and friend_id = ?";

        List<Integer> intList = jdbcTemplate.query(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        }, userId, friendId);
        if (intList.get(0) != 1) {
            throw new UserAlreadyExistException(String.format("Пользователь с идентификатором %d отсутствует в друзьях у %d", friendId, userId));
        }
            String sqlDelete = "delete from user_friend where user_id = ? and friend_id = ?";
            jdbcTemplate.update(sqlDelete, user.getId(), friend.getId());

        return user;
    }

    public List<User> getFriends(int userId) {
        User user = userDbStorage.findUserById(userId);
        String sql = "SELECT u.user_id, u.LOGIN , u.EMAIL , u.BIRTHDAY , u.NAME " +
                "FROM users AS u INNER JOIN user_friend AS uf ON u.user_id = uf.friend_id WHERE uf.user_id = ?";
        return jdbcTemplate.query(sql, userRowMapper(), userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userDbStorage.findUserById(userId);
        User otherUser = userDbStorage.findUserById(otherId);

        String sql = "SELECT  user_id, LOGIN, EMAIL, BIRTHDAY, NAME FROM users " +
                "WHERE user_id IN (SELECT FRIEND_ID FROM USER_FRIEND" +
                " WHERE user_id = ? OR user_id = ? GROUP BY FRIEND_ID HAVING COUNT(*) > 1)";

        return jdbcTemplate.query(sql, userRowMapper(), userId, otherId);
    }

    private RowMapper<User> userRowMapper() {
        return new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                return User.builder()
                        .id(rs.getInt("user_id"))
                        .email(rs.getString("email"))
                        .login(rs.getString("login"))
                        .name(rs.getString("name"))
                        .birthday(rs.getDate("birthday").toLocalDate())
                        .build();
            }
        };
    }

}

package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserDbStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, userRowMapper());
    }

    @Override
    public User create(User user) {
        validator(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Map<String, Object> userInMap = new HashMap<>(1);
        userInMap.put("login", user.getLogin());
        userInMap.put("email", user.getEmail());
        userInMap.put("birthday", user.getBirthday());
        userInMap.put("name", user.getName());

        int userId = simpleJdbcInsert.executeAndReturnKey(userInMap).intValue();
        user.setId(userId);

        return user;
    }

    @Override
    public User update(User user) {
        validator(user);
        String sqlFindUser = "select * from users where user_id = ?";
        List<User> userList = jdbcTemplate.query(sqlFindUser, userRowMapper(), user.getId());
        if (userList.size() != 1) {
            throw new UpdateException(String.format("Пользователь с идентификатором %d не существует.", user.getId()));
        }
        String sqlUpdate = "update users set login = ?, email = ?, birthday = ?, name = ? where user_id = ?";

        jdbcTemplate.update(sqlUpdate,
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getName(),
                user.getId()
        );
        return user;
    }

    @Override
    public User findUserById(int userId) {

        String sql = "select * from users where user_id = ?";
        List<User> userList = jdbcTemplate.query(sql, userRowMapper(), userId);
        if (userList.size() != 1) {
            throw new UpdateException(String.format("Пользователь с идентификатором %d не существует.", userId));
        }

        return userList.get(0);
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

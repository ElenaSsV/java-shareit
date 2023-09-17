package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private long id = 1L;

    @Override
    public User save(User user) {
        log.info("Creating user {}", user);
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Updating user {} ", user);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void delete(long userId) {
        log.info("Deleting user with id {}", userId);
        checkUserId(userId);
        users.remove(userId);

    }

    @Override
    public User getById(long userId) {
        log.info("Getting user with id {}", userId);
        checkUserId(userId);
        return users.get(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    private void checkUserId(long userId) {
        if (users.get(userId) == null) {
            log.debug("User with id {} is not found", userId);
            throw new NotFoundException("User is not found");
        }
    }
}

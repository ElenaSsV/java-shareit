package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public interface UserRepository {

    User save(User user);

    User update(User user);

    void delete(long userId);

    User getById(long userId);

    List<User> getAll();
}

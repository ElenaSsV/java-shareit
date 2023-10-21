package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceItTest {
    private final EntityManager em;
    private final UserService service;

    @Test
    public void saveUser() {
        UserDto userDto = UserMapper.toUserDto(getTestUser1());
        service.postUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void updateUser() throws JsonMappingException {
        User user = getTestUser1();

        em.createNativeQuery("insert into users (name, email) values (?, ?)")
                .setParameter(1, user.getName())
                .setParameter(2, user.getEmail())
                .executeUpdate();

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User returnedUser = query
                .setParameter("email", user.getEmail())
                .getSingleResult();

        User updatedUser = new User(returnedUser.getId(), "Updated test user", "test@email.ru");
        assertThat(service.partiallyUpdateUser(returnedUser.getId(), UserMapper.toUserDto(updatedUser)),
                equalTo(UserMapper.toUserDto(updatedUser)));
    }

    private User getTestUser1() {
        User user = new User();
        user.setId(1L);
        user.setName("Test user");
        user.setEmail("test@mail.ru");
        return user;
    }

    private User getTestUser2() {
        User user = new User();
        user.setId(2L);
        user.setName("Test user2");
        user.setEmail("test2@mail.ru");
        return user;
    }

}

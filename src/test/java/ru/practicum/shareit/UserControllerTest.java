package ru.practicum.shareit;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    private final UserService userService;

    private UserDto getTestUser1() {
       UserDto user1 = new UserDto();
       user1.setName("testUser1");
       user1.setEmail("test@test.ru");
       return user1;
    }

    private UserDto getTestUser2() {
        UserDto user2 = new UserDto();
        user2.setName("testUser2");
        user2.setEmail("test2@test.ru");
        return user2;
    }

    @Test
    public void postUserTest() {
        Optional<UserDto> postedUser = Optional.of(userService.postUser(getTestUser1()));

        assertThat(postedUser)
                .isPresent()
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("name", "testUser1"))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("email", "test@test.ru"));
    }

    @Test
    public void partiallyUpdateUser() throws JsonMappingException {
        UserDto user = userService.postUser(getTestUser1());

        UserDto toUpdate = new UserDto();
        toUpdate.setEmail("update@test.ru");

        Optional<UserDto> updatedUser = Optional.of(userService.partiallyUpdateUser(user.getId(), toUpdate));
        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("name", "testUser1"))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("email", "update@test.ru"));

        toUpdate.setName("UpdateName");
        Optional<UserDto> updatedUser2 = Optional.of(userService.partiallyUpdateUser(user.getId(), toUpdate));
        assertThat(updatedUser2)
                .isPresent()
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("name", "UpdateName"))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("email", "update@test.ru"));
    }

    @Test
    public void getByIdTest() {
        userService.postUser(getTestUser1());
        userService.postUser(getTestUser2());

        Optional<UserDto> userById2 = Optional.of(userService.getUserById(2));
        assertThat(userById2)
                .isPresent()
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("id", 2L))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("name", "testUser2"))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("email", "test2@test.ru"));
    }

    @Test
    public void getAll() {
        List<UserDto> emptyUsers = userService.getAllUsers();
        assertTrue(emptyUsers.isEmpty());

        userService.postUser(getTestUser1());
        userService.postUser(getTestUser2());

        List<UserDto> users = userService.getAllUsers();
        Optional<UserDto> user1 = Optional.of(users.get(0));

        assertThat(user1)
                .isPresent()
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("name", "testUser1"))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("email", "test@test.ru"));

        Optional<UserDto> user2 = Optional.of(users.get(1));
        assertThat(user2)
                .isPresent()
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("id", 2L))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("name", "testUser2"))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("email", "test2@test.ru"));
    }

    @Test
    public void deleteUserTest() {
        userService.postUser(getTestUser1());
        userService.postUser(getTestUser2());

        userService.deleteUserById(1);

        List<UserDto> users = userService.getAllUsers();
        assertEquals(1, users.size());

        Optional<UserDto> user2 = Optional.of(users.get(0));
        assertThat(user2)
                .isPresent()
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("id", 2L))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("name", "testUser2"))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("email", "test2@test.ru"));
    }


}

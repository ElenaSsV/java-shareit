package ru.practicum.shareit.user;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @Test
    public void postUser_whenUserWithSameEmailExists_thenUniqueEmailExceptionThrown() {
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(ConstraintViolationException.class);
        UniqueEmailException uniqueEmailException = assertThrows(UniqueEmailException.class,
                () -> userService.postUser(UserMapper.toUserDto(getTestUser1())));
    }

    @Test
    public void partiallyUpdateUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.partiallyUpdateUser(1, UserMapper.toUserDto(getTestUser1())));
    }

    @Test
    public void deleteUserById() {
       userService.deleteUserById(1);
    }

    @Test
    public void getUserById_whenNoUser_thenNotFoundExceptionThrown() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.getUserById(1));
    }

    @Test
    public void getUserById_whenUserIdIsCorrect_thenSuccess() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getTestUser1()));
        Optional<User> actualUser = userRepository.findById(1L);

        assertThat(actualUser)
                .isPresent()
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("name", "Test user"))
                .hasValueSatisfying(userDto -> assertThat(userDto).hasFieldOrPropertyWithValue("email", "test@mail.ru"));
    }

    @Test
    public void getAllUsers_whenEmpty() {
        Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserDto> actualUserList = userService.getAllUsers();
        assertTrue(actualUserList.isEmpty());
    }

    @Test
    public void getAllUsers() {
        Mockito.when(userRepository.findAll()).thenReturn(Collections.singletonList(getTestUser1()));

        List<UserDto> actualUserList = userService.getAllUsers();
        assertFalse(actualUserList.isEmpty());
        assertEquals(UserMapper.toUserDto(getTestUser1()), actualUserList.get(0));
    }


    private User getTestUser1() {
        User user = new User();
        user.setId(1L);
        user.setName("Test user");
        user.setEmail("test@mail.ru");
        return user;
    }

}

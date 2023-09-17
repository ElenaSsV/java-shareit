package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

  UserDto postUser(UserDto userDto);

  UserDto partiallyUpdateUser(long userId, UserDto userDto) throws JsonMappingException;

  void deleteUserById(long userId);

  UserDto getUserById(long userId);

  List<UserDto> getAllUsers();

}

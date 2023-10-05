package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

  UserDto postUser(UserDto userDto);

  UserDto partiallyUpdateUser(long userId, UserDto userDto) throws JsonMappingException;

  void deleteUserById(long userId);

  UserDto getUserById(long userId);

  List<UserDto> getAllUsers();

}

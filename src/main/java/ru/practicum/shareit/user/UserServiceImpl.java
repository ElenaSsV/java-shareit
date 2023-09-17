package ru.practicum.shareit.user;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public UserDto postUser(UserDto userDto) {
        if (userDto.getEmail() != null && getAllUsers().stream()
                .anyMatch(savedUser -> savedUser.getEmail().equals(userDto.getEmail()))) {
            log.info("User with email {} already exists", userDto.getEmail());
            throw new UniqueEmailException("User with email " + userDto.getEmail() + " already exists");
        }
        User userToPost = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(userToPost));
    }

    @Override
    public UserDto partiallyUpdateUser(long userId, UserDto userDto) throws JsonMappingException {
        User userToUpdate = userRepository.getById(userId);
        if (userDto.getEmail() != null && getAllUsers().stream()
                .filter(savedUser -> savedUser.getId() != userId)
                .anyMatch(savedUser -> savedUser.getEmail().equals(userDto.getEmail()))) {
            log.info("User with email {} already exists", userDto.getEmail());
            throw new UniqueEmailException("User with email " + userDto.getEmail() + " already exists");
        }
        objectMapper.updateValue(userToUpdate, userDto);
        return UserMapper.toUserDto(userToUpdate);

       // return UserMapper.toUserDto(userRepository.update(userToUpdate));
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.delete(userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.toUserDto(userRepository.getById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}

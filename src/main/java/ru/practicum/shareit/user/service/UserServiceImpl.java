package ru.practicum.shareit.user.service;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
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

    @Transactional
    @Override
    public UserDto postUser(UserDto userDto) {
        log.info("Posting user {}", userDto);
        User userToPost = UserMapper.toUser(userDto);
        User postedUser;

        try {
            postedUser = userRepository.save(userToPost);
        } catch (ConstraintViolationException e) {
            log.info("User with email {} already exists", userDto.getEmail());
            throw new UniqueEmailException("User with email " + userDto.getEmail() + " already exists");
        }
        return UserMapper.toUserDto(postedUser);
    }

    @Transactional
    @Override
    public UserDto partiallyUpdateUser(long userId, UserDto userDto) throws JsonMappingException {
        log.info("Updating user with id {} to {}", userId, userDto);
        User userToUpdate = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " is not found"));
        objectMapper.updateValue(userToUpdate, userDto);

        return UserMapper.toUserDto(userRepository.save(userToUpdate));
    }

    @Transactional
    @Override
    public void deleteUserById(long userId) {
        log.info("Deleting user with id {}", userId);
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long userId) {
        log.info("Retrieving user with id {}", userId);
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " is not found")));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}


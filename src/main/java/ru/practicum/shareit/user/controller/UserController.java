package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto postUser(@Valid @RequestBody UserDto userDto) {
        return userService.postUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable(value = "id") Long userId, @RequestBody UserDto userDto) throws JsonMappingException {
        return userService.partiallyUpdateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(value = "id") Long userId) {
        userService.deleteUserById(userId);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable(value = "id") Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

}
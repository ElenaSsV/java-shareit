package ru.practicum.shareit.exception;

public class UniqueEmailException extends RuntimeException {

    public UniqueEmailException(String message) {
        super(message);
    }
}

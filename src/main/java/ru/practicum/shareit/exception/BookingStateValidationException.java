package ru.practicum.shareit.exception;

public class BookingStateValidationException extends IllegalOperationException {

    public BookingStateValidationException(String message) {
        super(message);
    }
}
package ru.practicum.shareit.booking.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    ResponseBookingDto postBooking(long bookerId, RequestBookingDto bookingDto);

    ResponseBookingDto updateBookingStatus(long userId, boolean isApproved, long bookingId) throws JsonMappingException;

    ResponseBookingDto getBookingById(long userId, long bookingId);

    List<ResponseBookingDto> getAllBookingsByBooker(BookingState bookingState, long userId);

    List<ResponseBookingDto> getAllBookingsByItemOwner(BookingState bookingState, long userId);
}

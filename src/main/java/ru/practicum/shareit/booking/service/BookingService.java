package ru.practicum.shareit.booking.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import ru.practicum.shareit.booking.dto.RequestBooking;
import ru.practicum.shareit.booking.dto.ResponseBooking;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    ResponseBooking postBooking(long bookerId, RequestBooking bookingDto);

    ResponseBooking updateBookingStatus(long userId, boolean isApproved, long bookingId) throws JsonMappingException;

    ResponseBooking getBookingById(long userId, long bookingId);

    List<ResponseBooking> getAllBookingsByBooker(BookingState bookingState, long userId);

    List<ResponseBooking> getAllBookingsByItemOwner(BookingState bookingState, long userId);
}

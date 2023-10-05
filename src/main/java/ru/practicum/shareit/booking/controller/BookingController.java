package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingStateValidationException;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseBookingDto postBooking(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                          @RequestBody @Valid RequestBookingDto bookingDto) {
        return bookingService.postBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto updateBookingStatus(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                  @RequestParam("approved") Boolean isApproved,
                                                  @PathVariable long bookingId) throws JsonMappingException {
        return bookingService.updateBookingStatus(userId, isApproved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getItemById(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                          @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<ResponseBookingDto> getAllByBooker(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                   @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                                   String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookingStateValidationException("Unknown state: " + state);
        }
        return bookingService.getAllBookingsByBooker(bookingState, userId);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getAllByItemOwner(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                      @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                                      String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookingStateValidationException("Unknown state: " + state);
        }
        return bookingService.getAllBookingsByItemOwner(bookingState, userId);
    }
}

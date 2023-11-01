package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.RequestBooking;
import ru.practicum.shareit.booking.dto.ResponseBooking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseBooking postBooking(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                       @RequestBody RequestBooking bookingDto) {
        return bookingService.postBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBooking updateBookingStatus(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                               @RequestParam("approved") Boolean isApproved,
                                               @PathVariable long bookingId) throws JsonMappingException {
        return bookingService.updateBookingStatus(userId, isApproved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseBooking getItemById(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                       @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<ResponseBooking> getAllByBooker(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                                BookingState state,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {

        return bookingService.getAllBookingsByBooker(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<ResponseBooking> getAllByItemOwner(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                   @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                                   BookingState state,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {

        return bookingService.getAllBookingsByItemOwner(state, userId, from, size);
    }
}

package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class RequestBooking {
    @FutureOrPresent
    @NotNull
    private LocalDateTime start;
    @Future
    @NotNull
    private LocalDateTime end;
    @NotNull
    private Long itemId;
}

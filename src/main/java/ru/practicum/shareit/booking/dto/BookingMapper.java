package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking toBooking(RequestBookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }

    public static ResponseBookingDto toResponseBookingDto(Booking booking) {
        ResponseBookingDto bookingDto = new ResponseBookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        return bookingDto;
    }

    public static List<ResponseBookingDto> bookingDtoList(Iterable<Booking> bookings) {
        List<ResponseBookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toResponseBookingDto(booking));
        }
        return dtos;
    }

    public static BookingToItemDto toBookingToItemDto(Booking booking) {
        BookingToItemDto dto = new BookingToItemDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }

}
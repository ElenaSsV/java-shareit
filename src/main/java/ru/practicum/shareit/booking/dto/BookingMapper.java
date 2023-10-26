package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking toBooking(RequestBooking bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static ResponseBooking toResponseBooking(Booking booking) {
        ResponseBooking bookingDto = new ResponseBooking();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        return bookingDto;
    }

    public static List<ResponseBooking> toResponseBookingList(Iterable<Booking> bookings) {
        List<ResponseBooking> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toResponseBooking(booking));
        }
        return dtos;
    }

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }

}

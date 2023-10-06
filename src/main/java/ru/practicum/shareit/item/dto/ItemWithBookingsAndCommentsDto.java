package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingToItemDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Data
public class ItemWithBookingsAndCommentsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingToItemDto lastBooking;
    private BookingToItemDto nextBooking;
    private List<CommentDto> comments;
}

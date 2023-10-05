package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingToItemDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemWithBookingsAndCommentsDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
    private BookingToItemDto lastBooking;
    private BookingToItemDto nextBooking;
    private List<CommentDto> comments;
}

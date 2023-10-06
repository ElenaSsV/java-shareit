package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        if (item.getId() != null) {
            itemDto.setId(item.getId());
        }
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static ItemWithBookingsAndCommentsDto toItemWithBookingsAndCommentsDto(Item item, Optional<Booking> last,
                                                                                  Optional<Booking> next,
                                                                                  List<CommentDto> comments) {
        ItemWithBookingsAndCommentsDto itemDto = new ItemWithBookingsAndCommentsDto();
        if (item.getId() != null) {
            itemDto.setId(item.getId());
        }
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        last.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toBookingToItemDto(booking)));
        next.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toBookingToItemDto(booking)));
        itemDto.setComments(comments);
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        if (itemDto.getId() != null) {
            item.setId(itemDto.getId());
        }
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

}
package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndComments;

import java.util.List;

public interface ItemService {

    ItemDto postItem(long userId, ItemDto itemDto);

    CommentDto postComment(long userId, long itemId, CommentDto commentDto);

    ItemDto partiallyUpdateItem(long userId, long itemId, ItemDto itemDto) throws JsonMappingException;

    ItemWithBookingsAndComments getItemByIdAnyUser(long userId, long itemId);

    List<ItemWithBookingsAndComments> getAllItemsOwner(long userId, int from, int size);

    List<ItemDto> searchItem(long userId, String text, int from, int size);
}

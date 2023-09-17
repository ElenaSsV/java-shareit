package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {

    ItemDto postItem(long userId, ItemDto itemDto);

    ItemDto partiallyUpdateItem(long userId, long itemId, ItemDto itemDto) throws JsonMappingException;

    ItemDto getItemByIdAnyUser(long itemId);

    List<ItemDto> getAllItemsOwner(long userId);

    List<ItemDto> searchItem(long userId, String text);
}

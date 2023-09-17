package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ObjectMapper objectMapper;
    private final UserService userService;


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, ObjectMapper objectMapper, UserService userService) {
        this.itemRepository = itemRepository;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @Override
    public ItemDto postItem(long userId, ItemDto itemDto) {
        Item itemToPost = ItemMapper.toItem(itemDto);
        itemToPost.setOwner(UserMapper.toUser(userService.getUserById(userId)));
        return ItemMapper.toItemDto(itemRepository.save(userId, itemToPost));
    }

    @Override
    public ItemDto partiallyUpdateItem(long userId, long itemId, ItemDto itemDto) throws JsonMappingException {
        Item itemToUpdate = itemRepository.getById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Item not found");
        });
        if (itemToUpdate.getOwner().getId() != userId) {
            throw new NotFoundException("Item with id " + itemId + " for user with id " + userId + " not found");
        }
        objectMapper.updateValue(itemToUpdate, itemDto);
        return ItemMapper.toItemDto(itemToUpdate);
    }

    @Override
    public ItemDto getItemByIdAnyUser(long itemId) {
        Item searchedItem = itemRepository.getById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Item not found");
        });
        return ItemMapper.toItemDto(searchedItem);
    }

    @Override
    public List<ItemDto> getAllItemsOwner(long userId) {
        return itemRepository.getAll(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(long userId, String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchByName(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}

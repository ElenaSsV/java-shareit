package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(long ownerId, Item item);

    Optional<Item> getById(long itemId);

    List<Item> getAll(long userId);

    List<Item> searchByName(String searchedName);


}

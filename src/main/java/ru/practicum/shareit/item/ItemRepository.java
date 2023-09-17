package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Component
public interface ItemRepository {

    Item save(long ownerId, Item item);

    Optional<Item> getById(long itemId);

    List<Item> getAll(long userId);

    List<Item> searchByName(String searchedName);


}

package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;


@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final HashMap<Long, List<Item>> items = new HashMap<>();

    @Override
    public Item save(long ownerId, Item item) {
        log.info("Saving item {}", item);
        item.setId(getId());
        items.compute(ownerId, (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return item;
    }

    @Override
    public Optional<Item> getById(long itemId) {
        log.info("Getting item by id {}", itemId);
        return items.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst();
    }

    @Override
    public List<Item> getAll(long userId) {
        log.info("Getting all items of user with id {}", userId);
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public List<Item> searchByName(String searchedName) {
        log.info("Searching item which contains {}", searchedName);

        return  items.values().stream().flatMap(Collection::stream)
                .filter(item -> item.getName().toLowerCase().contains(searchedName.toLowerCase())
                        || item.getDescription().toLowerCase().contains(searchedName.toLowerCase())
                        && item.getAvailable())
                .collect(Collectors.toList());
    }

    private long getId() {
        long lastId = items.values()
                .stream()
                .flatMap(Collection::stream)
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}

package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto postItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                            @RequestBody @Valid ItemDto itemDto) {
        return itemService.postItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                       @RequestBody ItemDto itemDto,
                                       @PathVariable long itemId) throws JsonMappingException {
        return itemService.partiallyUpdateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        return itemService.getItemByIdAnyUser(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        return itemService.getAllItemsOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                    @RequestParam (required = true)  String text) {
        return itemService.searchItem(userId, text);
    }
}

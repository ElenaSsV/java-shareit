package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndComments;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto postItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                            @RequestBody @Valid ItemDto itemDto) {
        return itemService.postItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                  @PathVariable long itemId,
                                  @RequestBody @Valid CommentDto commentDto) {
        return itemService.postComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) throws JsonMappingException {
        return itemService.partiallyUpdateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsAndComments getItemById(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                                   @PathVariable long itemId) {
        return itemService.getItemByIdAnyUser(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingsAndComments> getAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = true)
                                                            long userId,
                                                         @RequestParam(defaultValue = "0") int from,
                                                         @RequestParam(defaultValue = "10")
                                                             int size) {
        return itemService.getAllItemsOwner(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                    @RequestParam (required = true)  String text,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        if (text.isEmpty() || text.isBlank()) {
            throw new ValidationException("Text cannot be empty");
        }
        return itemService.searchItem(userId, text, from, size);
    }
}
package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;

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

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                  @PathVariable long itemId,
                                  @RequestBody @Valid CommentDto commentDto) {
        return itemService.postComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                       @RequestBody ItemDto itemDto,
                                       @PathVariable long itemId) throws JsonMappingException {
        return itemService.partiallyUpdateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsAndCommentsDto getItemById(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                      @PathVariable long itemId) {
        return itemService.getItemByIdAnyUser(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingsAndCommentsDto> getAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        return itemService.getAllItemsOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                    @RequestParam (required = true)  String text) {
        return itemService.searchItem(userId, text);
    }
}

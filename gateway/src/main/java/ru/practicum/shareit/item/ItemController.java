package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                           @RequestBody @Valid ItemDto itemDto) {
        log.info("Posting item {} by user with id {}", itemDto, userId);
        return itemClient.postItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                              @PathVariable long itemId,
                                              @Valid @RequestBody  CommentDto commentDto) {
        log.info("Posting comment {} to item with id {} by user with id {}", commentDto, itemId, userId);
        return itemClient.postComment(commentDto, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable long itemId)  {
        log.info("Updating item with id {} to {} by user with id {}", itemId, itemDto, userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                                   @PathVariable long itemId) {
        log.info("Retrieving item with id {} by user with id {}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = true)
                                                         long userId,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                        @Positive @RequestParam(defaultValue = "10")
                                                         int size) {
        log.info("Retrieving all items by user with id {}, from {}, size {}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                    @RequestParam (required = true)  String text,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Searching items by text {}, from {}, size{}", text, from, size);
        return itemClient.searchItems(userId, text, from, size);
    }
}

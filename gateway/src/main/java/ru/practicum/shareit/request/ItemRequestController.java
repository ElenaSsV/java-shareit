package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                              @RequestBody @Valid ItemRequestDto request) {
        log.info("Posting item request {} by user with id {}", request, userId);
        return itemRequestClient.postItemRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        log.info("Retrieving requests by user with id {}", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                             @PathVariable Long requestId) {
        log.info("Retrieving request with id {} by user with id {}", requestId, userId);
        return itemRequestClient.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Retrieving all requests by user with id {}, from {}, size {}", userId, from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }
}

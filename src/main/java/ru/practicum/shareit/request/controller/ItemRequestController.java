package ru.practicum.shareit.request.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponseWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestResponse postRequest(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                           @RequestBody @Valid ItemRequestDto request) {
        return itemRequestService.postRequest(userId, request);
    }

    @GetMapping
    public List<ItemRequestResponseWithItems> getRequests(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        return itemRequestService.getRequestsByRequester(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseWithItems getRequestById(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                       @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseWithItems> getAllRequests(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                             @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.getAllRequestsByOtherUsers(userId, from, size);
    }
}

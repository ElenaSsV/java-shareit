package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponseWithItems;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponse postRequest(long userId, ItemRequestDto request);

    List<ItemRequestResponseWithItems> getRequestsByRequester(long userId);

    ItemRequestResponseWithItems getRequestById(long userId, long requestId);

    List<ItemRequestResponseWithItems> getAllRequestsByOtherUsers(long userId, int from, int size);
}

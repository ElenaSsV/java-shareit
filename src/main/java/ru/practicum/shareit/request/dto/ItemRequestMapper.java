package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequestResponse toResponseRequest(ItemRequest itemRequest) {
        ItemRequestResponse request = new ItemRequestResponse();
        request.setId(itemRequest.getId());
        request.setDescription(itemRequest.getDescription());
        request.setCreated(itemRequest.getCreated());
        return request;
    }

    public static ItemRequest toItemRequest(ItemRequestDto requestDto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        return request;
    }

    public static ItemRequestResponseWithItems toResponseRequestWithItems(ItemRequest itemRequest, List<ItemDto> responses) {
        ItemRequestResponseWithItems requests = new ItemRequestResponseWithItems();
        requests.setId(itemRequest.getId());
        requests.setDescription(itemRequest.getDescription());
        requests.setCreated(itemRequest.getCreated());
        requests.setItems(responses);
        return requests;
    }

}

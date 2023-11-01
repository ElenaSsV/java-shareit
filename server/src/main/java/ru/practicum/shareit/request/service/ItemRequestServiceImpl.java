package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponseWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public ItemRequestResponse postRequest(long userId, ItemRequestDto requestDto) {
        log.info("Posting ItemRequest {}", requestDto);

        User requestor = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " is not found"));
        ItemRequest requestToPost = ItemRequestMapper.toItemRequest(requestDto);
        log.info("Adding requestor {} to requestToPost", requestor);
        requestToPost.setRequestor(requestor);
        return ItemRequestMapper.toResponseRequest(itemRequestRepository.save(requestToPost));
    }

    @Override
    public List<ItemRequestResponseWithItems> getRequestsByRequester(long userId) {
        if (!userRepository.existsUserById(userId)) {
            throw new NotFoundException("User with id " + userId + " is not found");
        }
        Map<Long, ItemRequest> itemRequestMap = itemRequestRepository.findByRequestor_Id(userId)
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        Map<Long, List<Item>> responsesToRequestsMap = itemRepository.findAllByRequest_IdIn(itemRequestMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return itemRequestMap.values()
                .stream()
                .map(itemRequest -> ItemRequestMapper.toResponseRequestWithItems(itemRequest,
                        ItemMapper.toItemDtos(responsesToRequestsMap.getOrDefault(itemRequest.getId(),
                                Collections.emptyList()))))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseWithItems getRequestById(long userId, long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("ItemRequest with id " + requestId + " is not found"));

        if (!userRepository.existsUserById(userId)) {
            throw new NotFoundException("User with id " + userId + " is not found");
        }

        List<ItemDto> responsesToRequest = ItemMapper.toItemDtos(itemRepository.findByRequest_Id(requestId));
        return ItemRequestMapper.toResponseRequestWithItems(itemRequest, responsesToRequest);
    }

    @Override
    public List<ItemRequestResponseWithItems> getAllRequestsByOtherUsers(long userId, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by("created").descending());

        if (!userRepository.existsUserById(userId)) {
            throw new NotFoundException("User with id " + userId + " is not found");
        }

        Map<Long, ItemRequest> itemRequestMap = itemRequestRepository.findAllByRequestor_IdNot(userId, page)
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        Map<Long, List<Item>> responsesToRequestsMap = itemRepository.findAllByRequest_IdIn(itemRequestMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return itemRequestMap.values()
                .stream()
                .map(itemRequest -> ItemRequestMapper.toResponseRequestWithItems(itemRequest,
                        ItemMapper.toItemDtos(responsesToRequestsMap.getOrDefault(itemRequest.getId(),
                                Collections.emptyList()))))
                .collect(Collectors.toList());
    }
}

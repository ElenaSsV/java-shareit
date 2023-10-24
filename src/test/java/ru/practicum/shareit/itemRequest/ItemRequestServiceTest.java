package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponseWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private User owner;
    private User requestor;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    private void beforeEach() {
        owner = getTestUser1();
        item = getTestItem();
        requestor = getTestUser2();
        itemRequest = getTestItemRequest();
    }

    @Test
    public void postItemRequest_whenAllIsCorrect_thenSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto requestDto = new ItemRequestDto(itemRequest.getDescription());
        ItemRequestResponse responseRequest = itemRequestService.postRequest(requestor.getId(), requestDto);

        assertNotNull(responseRequest);
        assertEquals(itemRequest.getId(), responseRequest.getId());
        assertEquals(itemRequest.getDescription(), responseRequest.getDescription());
        assertEquals(itemRequest.getCreated(), responseRequest.getCreated());

        verify(itemRequestRepository).save(Mockito.any(ItemRequest.class));
    }

    @Test
    public void postRequest_whenUserIdIsIncorrect_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemRequestDto requestDto = new ItemRequestDto(itemRequest.getDescription());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.postRequest(99, requestDto));

        assertEquals("User with id 99 is not found", exception.getMessage());
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));

    }

    @Test
    public void getRequestByRequester_whenRequestorIdIsCorrect_thenSuccess() {
        when(userRepository.existsUserById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findByRequestor_Id(anyLong())).thenReturn(List.of(itemRequest));

        List<ItemRequestResponseWithItems> responseRequestWithItems = itemRequestService.getRequestsByRequester(requestor.getId());

        assertNotNull(responseRequestWithItems);
        assertEquals(1, responseRequestWithItems.size());
        assertEquals(itemRequest.getId(), responseRequestWithItems.get(0).getId());
        assertEquals(itemRequest.getDescription(), responseRequestWithItems.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), responseRequestWithItems.get(0).getCreated());
    }

    @Test
    public void getRequestsByRequestor_whenRequestorIdIsNotCorrect_thenNotFoundExceptionThrown() {
        when(userRepository.existsUserById(anyLong())).thenReturn(false);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getRequestsByRequester(99));

        assertEquals("User with id 99 is not found", exception.getMessage());
    }

    @Test
    public void getAllRequestsByOtherUsers() {
        User booker2 = new User(3L, "User3", "user3@email.ru");
        ItemRequest itemRequest2 = new ItemRequest(2L, "request description2", booker2,
                LocalDateTime.of(2022, 10, 14, 13, 44, 22));

        when(userRepository.existsUserById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByRequestor_IdNot(anyLong(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(itemRequest2)));

        Iterable<ItemRequestResponseWithItems> responseRequestWithItems = itemRequestService
                .getAllRequestsByOtherUsers(requestor.getId(), 0, 10);
        assertThat(responseRequestWithItems, iterableWithSize(equalTo(1)));
        assertThat(responseRequestWithItems, contains(ItemRequestMapper.toResponseRequestWithItems(itemRequest2, new ArrayList<>())));
    }

    @Test
    public void getRequestById() {
        when(userRepository.existsUserById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequest_Id(anyLong())).thenReturn(List.of(item));

        ItemRequestResponseWithItems responseRequestWithItems = itemRequestService.getRequestById(owner.getId(), itemRequest.getId());

        assertNotNull(responseRequestWithItems);

        assertEquals(1, responseRequestWithItems.getItems().size());
        assertEquals(itemRequest.getId(), responseRequestWithItems.getId());
        assertEquals(itemRequest.getDescription(), responseRequestWithItems.getDescription());
        assertEquals(itemRequest.getCreated(), responseRequestWithItems.getCreated());
    }


    @Test
    public void findByWrongRequestId() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getRequestById(requestor.getId(), 99));

        assertEquals("ItemRequest with id 99 is not found", exception.getMessage());
    }

    private Item getTestItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("Test item description");
        item.setAvailable(true);
        item.setOwner(new User(1L, "Test User", "test@mail.ru"));

        return item;
    }

    private User getTestUser1() {
        User user = new User();
        user.setId(1L);
        user.setName("Test user");
        user.setEmail("test@mail.ru");
        return user;
    }

    private User getTestUser2() {
        User user = new User();
        user.setId(2L);
        user.setName("Test user2");
        user.setEmail("test2@mail.ru");
        return user;
    }

    private ItemRequest getTestItemRequest() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Test request");
        itemRequest1.setRequestor(requestor);
        itemRequest1.setCreated(LocalDateTime.of(2023, 10, 20, 9, 0));
        return itemRequest1;
    }
}

package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IllegalOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ObjectMapper objectMapper;

    @Test
    public void postItem_whenUIncorrectUserId_thenNotFoundExceptionThrown() {
        long wrongUserId = 99L;
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(new NotFoundException("User not found"));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                itemService.postItem(wrongUserId, new ItemDto()));
    }

    @Test
    public void postItem_whenItemRequestIdIsNotNullAndIncorrectItemRequestId_thenNotFoundExceptionThrown() {
        Item itemWithRequest = getTestItemWithRequest();
        ItemDto itemDto = ItemMapper.toItemDto(itemWithRequest);

        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.postItem(3, itemDto));
    }

    @Test
    public void postItemWithRequest_whenAllIsCorrect_thenSuccess() {
        ItemDto itemDto = ItemMapper.toItemDto(getTestItemWithRequest());

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getTestUser1()));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getTestItemRequest()));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(getTestItemWithRequest());

        ItemDto actual = itemService.postItem(1, itemDto);
        assertEquals(itemDto, actual);
    }

    @Test
    public void postItem_whenAllIsCorrect_thenSuccess() {
        ItemDto itemDto = ItemMapper.toItemDto(getTestItem());
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getTestUser1()));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(getTestItem());

        ItemDto actualItem = itemService.postItem(1, itemDto);
        assertEquals(itemDto, actualItem);
    }

    @Test
    public void postComment_whenUserOrItemNotFound_thenNotFoundExceptionThrown() {
        long wrongUserId = 99L;
        long wrongItemId = 99L;

        IllegalOperationException illegalOperationException = assertThrows(IllegalOperationException.class, () ->
                itemService.postComment(wrongUserId, 1, new CommentDto()));

        IllegalOperationException illegalOperationException1 = assertThrows(IllegalOperationException.class, () ->
                itemService.postComment(1, wrongItemId, new CommentDto()));
    }


    @Test
    public void postComment_whenAllIsCorrect_thenSuccess() {
        Comment comment = getTestComment();
        Mockito.when(bookingRepository.existsBookingByItemIdAndBookerIdAndEndIsBefore(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any())).thenReturn(true);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getTestUser1()));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getTestItem()));
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);

        CommentDto expected = CommentMapper.toCommentDto(comment);
        CommentDto actual = itemService.postComment(1, 1, expected);
        assertEquals(expected, actual);

    }

    @Test
    public void partiallyUpdateItem_whenItemFound_thenSuccess() throws JsonMappingException {
        long itemId = 1L;
        long userId = 1L;

        Item oldItem = getTestItem();
        Item newItem = getTestItem();
        newItem.setAvailable(false);
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(false);

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(oldItem));
        Mockito.when(objectMapper.updateValue(Mockito.any(), Mockito.any())).thenReturn(newItem);
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(newItem);

        ItemDto actualItem = itemService.partiallyUpdateItem(userId, itemId, itemDto);

        assertEquals(false, actualItem.getAvailable());
        assertEquals(ItemMapper.toItemDto(newItem), actualItem);
    }

    @Test
    public void partiallyUpdateItem_whenItemNotFound_thenNotFoundExceptionThrown() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        ItemDto itemDto = ItemMapper.toItemDto(getTestItem());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.partiallyUpdateItem(1, 1, itemDto));
    }

    @Test
    public void partiallyUpdateItem_whenIsNotOwner_thenNotFoundExceptionThrown() {
        Item item = getTestItem();
        ItemDto itemDto = ItemMapper.toItemDto(item);

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.partiallyUpdateItem(99, 1, itemDto));
    }

    @Test
    public void getItemByIdAnyUser_whenLastAndNextBookingEmpty() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getTestItem()));
        Mockito.when(commentRepository.findAllByItemId(Mockito.anyLong())).thenReturn(List.of(getTestComment()));

        Optional<ItemWithBookingsAndComments> returnedItem = Optional.of(itemService.getItemByIdAnyUser(1, 1));

        assertThat(returnedItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("name", "Test item"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("description", "Test item description"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("available", true))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("comments",
                        List.of(CommentMapper.toCommentDto(getTestComment()))));
    }

    @Test
    public void getItemByIdAnyUser_whenItemIsNotFound_thenNotFoundExceptionThrown() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, ()
                -> itemService.getItemByIdAnyUser(1, 1));
    }

    @Test
    public void getAllByOwner_whenOneItemWithoutBookingsAndComments() {
        int from = 0;
        int size = 10;
        Page<Item> items = new PageImpl<>(Collections.singletonList(getTestItem()));
        Mockito.when(itemRepository.findItemByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(items);
        List<ItemWithBookingsAndComments> returnedItems = itemService.getAllItemsOwner(1L, from, size);

        assertFalse(returnedItems.isEmpty());
        assertEquals(returnedItems.get(0), ItemMapper.toItemWithBookingsAndCommentsDto(getTestItem(),
                Optional.empty(), Optional.empty(), new ArrayList<>()));

    }

    @Test
    public void getAllByOwner_whenOneItemWithOneBookingsAndNoComments() {
        int from = 0;
        int size = 10;
        Page<Item> items = new PageImpl<>(Collections.singletonList(getTestItem()));
        Mockito.when(itemRepository.findItemByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(items);
        Mockito.when(bookingRepository.findByItem_IdIn(Mockito.anySet()))
                .thenReturn(Collections.singletonList(getTestBooking()));
        ItemWithBookingsAndComments expectedItem = ItemMapper.toItemWithBookingsAndCommentsDto(getTestItem(),
                Optional.empty(), Optional.of(getTestBooking()), new ArrayList<>());
        List<ItemWithBookingsAndComments> actualItemList = itemService.getAllItemsOwner(1, from, size);

        assertFalse(actualItemList.isEmpty());
        assertEquals(expectedItem, actualItemList.get(0));
    }

    @Test
    public void getAllByOwner_whenNoItems_thenReturnEmptyList() {
        int from = 0;
        int size = 10;
        Page<Item> items = new PageImpl<>(new ArrayList<>());

        Mockito.when(itemRepository.findItemByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(items);

        List<ItemWithBookingsAndComments> actualItemList = itemService.getAllItemsOwner(1, from, size);
        assertTrue(actualItemList.isEmpty());
    }

    @Test
    public void searchItem_whenTextIsEmpty_thenEmptyListReturned() {
        List<ItemDto> emptyList = itemService.searchItem(1, "", 0, 10);
        assertTrue(emptyList.isEmpty());
    }

    @Test
    public void searchItem_whenAllIsCorrect_thenSuccess() {
        Item item = getTestItem();
        ItemDto itemDto = ItemMapper.toItemDto(item);

        Mockito.when(itemRepository.search(Mockito.anyString(), Mockito.any())).thenReturn(new PageImpl<>(List.of(item)));
        List<ItemDto> actual = itemService.searchItem(1, "test", 0, 10);
        assertThat(actual, hasSize(1));
        assertThat(actual, contains(itemDto));
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

    private Item getTestItemWithRequest() {
        Item item = new Item();
        item.setId(2L);
        item.setName("Test item2");
        item.setDescription("Test item2 description");
        item.setAvailable(true);
        item.setOwner(new User(1L, "Test User", "test@mail.ru"));
        item.setRequest(getTestItemRequest());
        return item;
    }

    private ItemRequest getTestItemRequest() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Test description");
        request.setRequestor(getTestUser2());
        request.setCreated(LocalDateTime.of(2023, 10, 15, 9, 0));
        return request;
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

    private Comment getTestComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(getTestItem());
        comment.setText("Test comment");
        comment.setAuthor(getTestUser2());
        comment.setCreated(LocalDateTime.of(2023, 10,25, 9,0));
        return comment;
    }

    private Booking getTestBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, 11, 25, 9, 0));
        booking.setEnd(LocalDateTime.of(2023, 11, 25, 10, 0));
        booking.setItem(getTestItem());
        booking.setBooker(getTestUser2());
        booking.setStatus(BookingStatus.APPROVED);

        return booking;
    }
}



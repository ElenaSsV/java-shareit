package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        itemDto = ItemMapper.toItemDto(getTestItem());
    }

    @Test
    public void postItem() throws Exception {
        when(itemService.postItem(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(itemService).postItem(1, itemDto);
    }

    @Test
    public void partiallyUpdateItem() throws Exception {
        ItemDto updatedItem = ItemMapper.toItemDto(getTestItem());
        updatedItem.setAvailable(false);

        when(itemService.partiallyUpdateItem(anyLong(), anyLong(), any()))
                .thenReturn(updatedItem);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(updatedItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItem.getName())))
                .andExpect(jsonPath("$.description", is(updatedItem.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItem.getAvailable())));
        verify(itemService).partiallyUpdateItem(1, 1, updatedItem);
    }

    @Test
    public void getItemByIdAnyUser() throws Exception {
        ItemWithBookingsAndComments itemResponseDto = ItemMapper.toItemWithBookingsAndCommentsDto(getTestItem(),
                Optional.empty(), Optional.empty(), new ArrayList<>());

        when(itemService.getItemByIdAnyUser(anyLong(), anyLong()))
                .thenReturn(itemResponseDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName())))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponseDto.getAvailable())));
        verify(itemService).getItemByIdAnyUser(1, 1);
    }

    @Test
    public void searchItem() throws Exception {
        List<ItemDto> itemDtos = List.of(itemDto);
        Mockito.doReturn(itemDtos).when(itemService).searchItem(anyLong(), anyString(), anyInt(), anyInt());

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "test")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
        verify(itemService).searchItem(1, "test", 0, 5);
    }

    @Test
    public void searchItems_thenTextIsBlank() throws Exception {
        when(itemService.searchItem(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", ""))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).searchItem(1, "", 0, 10);
    }

    @Test
    public void getAllItemsOwner() throws Exception {
        ItemWithBookingsAndComments responseDto = ItemMapper.toItemWithBookingsAndCommentsDto(getTestItem(),
                Optional.empty(), Optional.empty(), new ArrayList<>());

        List<ItemWithBookingsAndComments> items = List.of(responseDto);
        when(itemService.getAllItemsOwner(anyLong(), anyInt(), anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(responseDto.getName())))
                .andExpect(jsonPath("$[0].description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(responseDto.getAvailable())));
        verify(itemService).getAllItemsOwner(1, 0, 10);
    }

    @Test
    public void postComment() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "text comment", "author1",
                LocalDateTime.of(2023, 10, 25, 9, 0, 0));
        when(itemService.postComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));
        verify(itemService).postComment(1, 1, commentDto);
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
        booking.setStart(LocalDateTime.of(2023, 10, 25, 9, 0));
        booking.setEnd(LocalDateTime.of(2023, 10, 25, 10, 0));
        booking.setItem(getTestItem());
        booking.setBooker(getTestUser2());
        booking.setStatus(BookingStatus.APPROVED);

        return booking;
    }

}

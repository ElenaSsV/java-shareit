package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponseWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private ItemRequestResponseWithItems responseRequestWithItems;
    private ItemRequestDto requestDto;
    private ItemRequestResponse responseRequest;

    @BeforeEach
    public void beforeEach() {
        responseRequestWithItems = ItemRequestMapper.toResponseRequestWithItems(getTestItemRequest(), new ArrayList<>());
        requestDto = new ItemRequestDto(responseRequestWithItems.getDescription());
        responseRequest = ItemRequestMapper.toResponseRequest(getTestItemRequest());
    }

    @Test
    public void postRequest() throws Exception {
        when(itemRequestService.postRequest(anyLong(), any()))
                .thenReturn(responseRequest);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(responseRequest.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:SS")))));
    }

    @Test
    public void postRequest_whenDescriptionIsEmpty_thenThrowException() throws Exception {
        responseRequest.setDescription("");

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(responseRequest))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getRequestsByRequester() throws Exception {
        when(itemRequestService.getRequestsByRequester(anyLong()))
                .thenReturn(List.of(responseRequestWithItems));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(responseRequestWithItems))));
    }

    @Test
    public void getAllRequestsByOtherUsers() throws Exception {

        ItemRequestResponseWithItems responseRequestWithItems2 = ItemRequestMapper
                .toResponseRequestWithItems(getTestItemRequest(), new ArrayList<>());
        responseRequestWithItems2.setId(2L);

        when(itemRequestService.getAllRequestsByOtherUsers(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(responseRequestWithItems2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(responseRequestWithItems2))));
    }

    @Test
    public void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(responseRequestWithItems);

        mvc.perform(get("/requests/888")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseRequestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseRequestWithItems.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(responseRequestWithItems.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:SS")))));
    }

    private ItemRequest getTestItemRequest() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Test request");
        itemRequest1.setRequestor(new User(1L, "Test user2", "test2@mail.ru"));
        itemRequest1.setCreated(LocalDateTime.of(2023, 10, 15, 9, 0));
        return itemRequest1;
    }
}

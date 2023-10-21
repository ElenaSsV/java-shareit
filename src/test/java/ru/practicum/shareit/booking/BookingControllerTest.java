package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.RequestBooking;
import ru.practicum.shareit.booking.dto.ResponseBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private ResponseBooking responseBooking;
    private User user;
    private Item item;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    public void beforeEach() {
        user = getTestOwner();
        item = getTestItem();
        responseBooking = BookingMapper.toResponseBooking(getTestBooking());
    }

    @Test
    public void postBooking() throws Exception {
        RequestBooking bookingRequestDto = new RequestBooking(responseBooking.getStart(), responseBooking.getEnd(),
                user.getId());
        when(bookingService.postBooking(anyLong(), any()))
                .thenReturn(responseBooking);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBooking.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(responseBooking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(responseBooking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(responseBooking.getStatus().toString())));

        verify(bookingService, times(1)).postBooking(anyLong(), any());
    }

    @Test
    public void updateBookingStatus() throws Exception {
        responseBooking.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateBookingStatus(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(responseBooking);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBooking.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(responseBooking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(responseBooking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(responseBooking.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(responseBooking.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(responseBooking.getStatus().toString())));
    }

    @Test
    public void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(responseBooking);

        mvc.perform(get("/bookings/99")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBooking.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(responseBooking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(responseBooking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(responseBooking.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(responseBooking.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(responseBooking.getStatus().toString())));
    }

    @Test
    public void getAllByBooker() throws Exception {
        when(bookingService.getAllBookingsByBooker(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(responseBooking));

        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseBooking.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(responseBooking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(responseBooking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(responseBooking.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(responseBooking.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(responseBooking.getStatus().toString())));
    }

    @Test
    public void getAllByItemOwner() throws Exception {
        when(bookingService.getAllBookingsByItemOwner(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(responseBooking));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseBooking.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(responseBooking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(responseBooking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(responseBooking.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(responseBooking.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(responseBooking.getStatus().toString())));

    }

    private Booking getTestBooking() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.of(2023, 10, 25, 9, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 25, 10, 0, 0);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(getTestItem());
        booking.setBooker(getTestBooker());
        booking.setStatus(BookingStatus.WAITING);

        return booking;
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

    private User getTestOwner() {
        User user = new User();
        user.setId(1L);
        user.setName("Test user");
        user.setEmail("test@mail.ru");
        return user;
    }

    private User getTestBooker() {
        User user = new User();
        user.setId(2L);
        user.setName("Test user2");
        user.setEmail("test2@mail.ru");
        return user;
    }

}

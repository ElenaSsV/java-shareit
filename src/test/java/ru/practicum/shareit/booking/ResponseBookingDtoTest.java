package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.ResponseBooking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ResponseBookingDtoTest {

    @Autowired
    private JacksonTester<ResponseBooking> json;

    @Test
    public void testBookingResponseDto() throws IOException {

        var owner = getTestOwner();
        var item = getTestItem();

        var booker = getTestBooker();

        ResponseBooking responseBooking = getTestBooking();

        var res = json.write(responseBooking);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.start");
        assertThat(res).hasJsonPath("$.end");
        assertThat(res).hasJsonPath("$.item");
        assertThat(res).hasJsonPath("$.booker");
        assertThat(res).hasJsonPath("$.status");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(responseBooking.getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.start")
                .isEqualTo(responseBooking.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(res).extractingJsonPathStringValue("$.end")
                .isEqualTo(responseBooking.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(res).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(responseBooking.getItem().getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(responseBooking.getItem().getName());
        assertThat(res).extractingJsonPathStringValue("$.item.description")
                .isEqualTo(responseBooking.getItem().getDescription());
        assertThat(res).extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(responseBooking.getItem().getAvailable());
        assertThat(res).extractingJsonPathStringValue("$.item.requestId").isNullOrEmpty();
        assertThat(res).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(responseBooking.getBooker().getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(responseBooking.getBooker().getName());
        assertThat(res).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo(responseBooking.getBooker().getEmail());
        assertThat(res).extractingJsonPathStringValue("$.status")
                .isEqualTo(responseBooking.getStatus().toString());
    }

    private ResponseBooking getTestBooking() {
        ResponseBooking booking = new ResponseBooking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, 10, 25, 9, 0));
        booking.setEnd(LocalDateTime.of(2023, 10, 25, 10, 0));
        booking.setItem(new ItemDto(1L, "Test item", "Test item description", true, null));
        booking.setBooker(new UserDto(3L, "Test user2", "test3@mail.ru"));
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
        user.setId(2L);
        user.setName("Test user2");
        user.setEmail("test2@mail.ru");
        return user;
    }

    private User getTestBooker() {
        User user = new User();
        user.setId(3L);
        user.setName("Test user3");
        user.setEmail("test3@mail.ru");
        return user;
    }
}

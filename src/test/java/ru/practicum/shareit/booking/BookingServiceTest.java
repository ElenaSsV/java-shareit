package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.RequestBooking;
import ru.practicum.shareit.booking.dto.ResponseBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @Test
    public void postBooking() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getTestItem()));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getTestBooker()));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(getTestBooking());

        Booking testBooking = getTestBooking();
        RequestBooking requestBooking = new RequestBooking(testBooking.getStart(),
                testBooking.getEnd(), testBooking.getItem().getId());

        ResponseBooking booking = bookingService.postBooking(2, requestBooking);
        assertEquals(BookingMapper.toResponseBooking(testBooking), booking);
        testBooking.setId(0);
        verify(bookingRepository).save(testBooking);
    }

    @Test
    public void getBookingById_whenIdIncorrect_thenNotFoundException() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenThrow(NotFoundException.class);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1, 5));
    }

    @Test
    public void getBookingByCorrectId() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getTestBooking()));

        Optional<ResponseBooking> booking = Optional.of(bookingService.getBookingById(1, 5));
        assertEquals(BookingMapper.toResponseBooking(getTestBooking()), booking.get());
    }

    @Test
    public void getAllBookingsByBooker_thenBookerIdIsIncorrect_thenNotFoundExceptionThrown() {
        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenThrow(NotFoundException.class);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByBooker(BookingState.ALL, 1, 0, 10));
    }

    @Test
    public void getAllBookingsByOwner_thenOwnerIdIsIncorrect_thenNotFoundExceptionThrown() {
        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenThrow(NotFoundException.class);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByItemOwner(BookingState.ALL, 1, 0, 10));
    }

    private Booking getTestBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, 10, 25, 9, 0));
        booking.setEnd(LocalDateTime.of(2023, 10, 25, 10, 0));
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

    private User getTestBooker() {
        User user = new User();
        user.setId(2L);
        user.setName("Test user2");
        user.setEmail("test2@mail.ru");
        return user;
    }
}

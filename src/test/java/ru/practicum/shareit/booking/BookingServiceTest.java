package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.RequestBooking;
import ru.practicum.shareit.booking.dto.ResponseBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.IllegalOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

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
    public void postBooking_whenIncorrectItemId_thenThrowNotFoundException() {
        RequestBooking requestBooking = new RequestBooking(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.postBooking(1, requestBooking));
        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    public void postBooking_whenItemIsNotAvailable_thenIllegalOperationExceptionThrown() {
        Item item = getTestItem();
        item.setAvailable(false);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getTestBooker()));

        Booking testBooking = getTestBooking();
        RequestBooking requestBooking = new RequestBooking(testBooking.getStart(),
                testBooking.getEnd(), testBooking.getItem().getId());
        IllegalOperationException illegalOperationException = assertThrows(IllegalOperationException.class,
                () -> bookingService.postBooking(2, requestBooking));
        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    public void getBookingById_whenIdIncorrect_thenNotFoundException() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenThrow(NotFoundException.class);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1, 5L));
        verify(bookingRepository).findById(5L);
    }

    @Test
    public void getBookingByCorrectId() {
        long userId = 1;
        long bookingId = 5;
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getTestBooking()));

        Optional<ResponseBooking> booking = Optional.of(bookingService.getBookingById(userId, bookingId));
        assertEquals(BookingMapper.toResponseBooking(getTestBooking()), booking.get());
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    public void updateBookingStatus_whenAllIsCorrect_thenSuccess() {
        Booking booking = getTestBooking();
        Booking updatedBooking = getTestBooking();
        updatedBooking.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(updatedBooking);


        ResponseBooking actual = bookingService.updateBookingStatus(booking.getItem().getOwner().getId(),
                true, booking.getId());
        ResponseBooking expected = BookingMapper.toResponseBooking(updatedBooking);
        assertEquals(expected, actual);
        verify(bookingRepository).save(any());
    }

    @Test
    public void updateBookingStatus_whenItemOwnerIdIsIncorrect_thenNotFoundExceptionThrown() {
        Mockito.when(bookingRepository.findByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.updateBookingStatus(99, true, 1));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void updateBookingStatus_whenBookingStatusAlreadyApproved_thenIllegalOperationExceptionThrown() {
        Booking booking = getTestBooking();
        booking.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        IllegalOperationException illegalOperationException = assertThrows(IllegalOperationException.class,
                () -> bookingService.updateBookingStatus(booking.getItem().getOwner().getId(),
                        true, booking.getId()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void updateBookingStatus_whenBookingStatusAlreadyRejected_thenIllegalOperationExceptionThrown() {
        Booking booking = getTestBooking();
        booking.setStatus(BookingStatus.REJECTED);

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        IllegalOperationException illegalOperationException = assertThrows(IllegalOperationException.class,
                () -> bookingService.updateBookingStatus(booking.getItem().getOwner().getId(),
                        false, booking.getId()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void getAllBookingsByBooker_thenBookerIdIsIncorrect_thenNotFoundExceptionThrown() {
        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenThrow(NotFoundException.class);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByBooker(BookingState.ALL, 1, 0, 10));
    }

    @Test
    public void getAllBookingsByBooker_whenFromOrSizeIsIncorrect_thenValidationExceptionThrown() {
        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsByBooker(BookingState.ALL, 1, -1, 10));
    }

    @Test
    public void getAllBookingsByBooker_whenStatePast() {
        Booking pastBooking = getTestBooking2();

        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(Mockito.anyLong(), Mockito.any(),
                Mockito.any())).thenReturn(new PageImpl<>(List.of(pastBooking)));

        Iterable<ResponseBooking> actualBooking = bookingService.getAllBookingsByBooker(BookingState.PAST,
                pastBooking.getBooker().getId(), 0, 10);
        assertThat(actualBooking, iterableWithSize(1));
        assertThat(actualBooking, contains(BookingMapper.toResponseBooking(pastBooking)));

        verify(bookingRepository).findByBooker_IdAndEndIsBeforeOrderByStartDesc(Mockito.anyLong(), Mockito.any(),
                Mockito.any());
    }

    @Test
    public void getAllBookingsByBooker_whenStateCurrent() {
        Booking currentBooking = getTestBooking4();

        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                .findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Mockito.anyLong(), Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(new PageImpl<>(List.of(currentBooking)));

        Iterable<ResponseBooking> actualBooking = bookingService.getAllBookingsByBooker(BookingState.CURRENT,
                currentBooking.getBooker().getId(), 0, 10);
        assertThat(actualBooking, iterableWithSize(1));
        assertThat(actualBooking, contains(BookingMapper.toResponseBooking(currentBooking)));

        verify(bookingRepository).findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Mockito.anyLong(), Mockito.any(), Mockito.any(),
                Mockito.any());
    }

    @Test
    public void getAllBookingsByBooker_whenStateFuture() {
        Booking futureBooking = getTestBooking();

        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                .findByBooker_IdAndStartIsAfterOrderByStartDesc(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(futureBooking)));

        Iterable<ResponseBooking> actualBooking = bookingService.getAllBookingsByBooker(BookingState.FUTURE,
                futureBooking.getBooker().getId(), 0, 10);
        assertThat(actualBooking, iterableWithSize(1));
        assertThat(actualBooking, contains(BookingMapper.toResponseBooking(futureBooking)));

        verify(bookingRepository).findByBooker_IdAndStartIsAfterOrderByStartDesc(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    public void getAllBookingsByBooker_whenStateWaiting() {
        Booking bookingRejected = getTestBooking2();

        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findByBooker_IdAndStatusOrderByStartDesc(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(bookingRejected)));

        Iterable<ResponseBooking> actualBooking = bookingService.getAllBookingsByBooker(BookingState.REJECTED,
                bookingRejected.getBooker().getId(), 0, 10);
        assertThat(actualBooking, iterableWithSize(1));
        assertThat(actualBooking, contains(BookingMapper.toResponseBooking(bookingRejected)));

        verify(bookingRepository).findByBooker_IdAndStatusOrderByStartDesc(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    public void getAllBookingsByBooker_whenStateAll() {
        Booking booking = getTestBooking2();

        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findByBooker_IdOrderByStartDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        Iterable<ResponseBooking> actualBooking = bookingService.getAllBookingsByBooker(BookingState.ALL,
                booking.getBooker().getId(), 0, 10);
        assertThat(actualBooking, iterableWithSize(1));
        assertThat(actualBooking, contains(BookingMapper.toResponseBooking(booking)));

        verify(bookingRepository).findByBooker_IdOrderByStartDesc(Mockito.anyLong(), Mockito.any());
    }

    @Test
    public void getAllBookingsByBooker_whenStateRejected() {
        Booking bookingWaiting = getTestBooking();

        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findByBooker_IdAndStatusOrderByStartDesc(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(bookingWaiting)));

        Iterable<ResponseBooking> actualBooking = bookingService.getAllBookingsByBooker(BookingState.WAITING,
                bookingWaiting.getBooker().getId(), 0, 10);
        assertThat(actualBooking, iterableWithSize(1));
        assertThat(actualBooking, contains(BookingMapper.toResponseBooking(bookingWaiting)));

        verify(bookingRepository).findByBooker_IdAndStatusOrderByStartDesc(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    public void getAllBookingsByOwner_thenOwnerIdIsIncorrect_thenNotFoundExceptionThrown() {
        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenThrow(NotFoundException.class);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByItemOwner(BookingState.ALL, 1, 0, 10));

    }

    @Test
    public void getAllBookingsByItemOwner_whenFromOrSizeIsIncorrect_thenValidationExceptionThrown() {
        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsByItemOwner(BookingState.ALL, 1, -1, 10));
    }

    @Test
    public void getAllBookingsByItemOwner_whenStatePast() {
        Booking pastBooking = getTestBooking2();

        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findByOwner_IdAndStatePast(Mockito.anyLong(), Mockito.any(),
                Mockito.any())).thenReturn(new PageImpl<>(List.of(pastBooking)));

        Iterable<ResponseBooking> actualBooking = bookingService.getAllBookingsByItemOwner(BookingState.PAST,
                pastBooking.getBooker().getId(), 0, 10);
        assertThat(actualBooking, iterableWithSize(1));
        assertThat(actualBooking, contains(BookingMapper.toResponseBooking(pastBooking)));

        verify(bookingRepository).findByOwner_IdAndStatePast(Mockito.anyLong(), Mockito.any(),
                Mockito.any());
    }

    @Test
    public void getAllBookingsByItemOwner_whenStateCurrent() {
        Booking currentBooking = getTestBooking4();

        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                .findByOwner_IdAndStateCurrent(Mockito.anyLong(), Mockito.any(), Mockito.any(),
                        Mockito.any())).thenReturn(new PageImpl<>(List.of(currentBooking)));

        Iterable<ResponseBooking> actualBooking = bookingService.getAllBookingsByItemOwner(BookingState.CURRENT,
                currentBooking.getBooker().getId(), 0, 10);
        assertThat(actualBooking, iterableWithSize(1));
        assertThat(actualBooking, contains(BookingMapper.toResponseBooking(currentBooking)));

        verify(bookingRepository).findByOwner_IdAndStateCurrent(Mockito.anyLong(), Mockito.any(), Mockito.any(),
                Mockito.any());
    }

    @Test
    public void getAllBookingsByItemOwner_whenStateFuture() {
        Booking futureBooking = getTestBooking();

        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findByOwner_IdAndStateFuture(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(futureBooking)));

        Iterable<ResponseBooking> actualBooking = bookingService.getAllBookingsByItemOwner(BookingState.FUTURE,
                futureBooking.getBooker().getId(), 0, 10);
        assertThat(actualBooking, iterableWithSize(1));
        assertThat(actualBooking, contains(BookingMapper.toResponseBooking(futureBooking)));

        verify(bookingRepository).findByOwner_IdAndStateFuture(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    public void getAllBookingsByItemOwner_whenStateWaiting() {
        Booking bookingRejected = getTestBooking2();

        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findByOwner_IdAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(bookingRejected)));

        Iterable<ResponseBooking> actualBooking = bookingService.getAllBookingsByItemOwner(BookingState.REJECTED,
                bookingRejected.getBooker().getId(), 0, 10);
        assertThat(actualBooking, iterableWithSize(1));
        assertThat(actualBooking, contains(BookingMapper.toResponseBooking(bookingRejected)));

        verify(bookingRepository).findByOwner_IdAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    public void getAllBookingsByItemOwner_whenStateAll() {
        Booking booking = getTestBooking2();

        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findByOwner_IdOrderByStartDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        Iterable<ResponseBooking> actualBooking = bookingService.getAllBookingsByItemOwner(BookingState.ALL,
                booking.getBooker().getId(), 0, 10);
        assertThat(actualBooking, iterableWithSize(1));
        assertThat(actualBooking, contains(BookingMapper.toResponseBooking(booking)));

        verify(bookingRepository).findByOwner_IdOrderByStartDesc(Mockito.anyLong(), Mockito.any());
    }

    @Test
    public void getAllBookingsByItemOwner_whenStateRejected() {
        Booking bookingWaiting = getTestBooking();

        Mockito.when(userRepository.existsUserById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findByOwner_IdAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(bookingWaiting)));

        Iterable<ResponseBooking> actualBooking = bookingService.getAllBookingsByItemOwner(BookingState.WAITING,
                bookingWaiting.getBooker().getId(), 0, 10);
        assertThat(actualBooking, iterableWithSize(1));
        assertThat(actualBooking, contains(BookingMapper.toResponseBooking(bookingWaiting)));

        verify(bookingRepository).findByOwner_IdAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any());
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

    private Booking getTestBooking2() {
        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStart(LocalDateTime.of(2023, 10, 15, 9, 0));
        booking.setEnd(LocalDateTime.of(2023, 10, 15, 10, 0));
        booking.setItem(getTestItem());
        booking.setBooker(getTestBooker());
        booking.setStatus(BookingStatus.REJECTED);

        return booking;
    }

    private Booking getTestBooking3() {
        Booking booking = new Booking();
        booking.setId(3L);
        booking.setStart(LocalDateTime.of(2023, 10, 26, 9, 0));
        booking.setEnd(LocalDateTime.of(2023, 10, 26, 10, 0));
        booking.setItem(getTestItem());
        booking.setBooker(getTestBooker());
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    private Booking getTestBooking4() {
        Booking booking = new Booking();
        booking.setId(4L);
        booking.setStart(LocalDateTime.of(2023, 10, 20, 9, 0));
        booking.setEnd(LocalDateTime.of(2023, 10, 30, 10, 0));
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

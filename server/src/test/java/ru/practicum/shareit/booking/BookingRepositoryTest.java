package ru.practicum.shareit.booking;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
   public void beforeEach() {
        booking1 = getTestBooking1();
        booking2 = getTestBooking2();

        em.persist(getTestOwner());
        em.persist(getTestBooker1());
        em.persist(getTestBooker2());
        em.persist(getTestItem());
        em.persist(getTestBooking1());
        em.persist(getTestBooking2());
    }

    @Test
    public void findByBooker_IdOrderByStartDesc() {
        Iterable<Booking> bookings = bookingRepository.findByBooker_IdOrderByStartDesc(booking1.getBooker().getId(),
                PageRequest.ofSize(10));
        assertNotNull(bookings);
        assertThat(bookings, iterableWithSize(equalTo(2)));
        booking1.setId(1);
        booking2.setId(2);

        assertThat(bookings, containsInRelativeOrder(booking2, booking1));
    }

    @Test
    public void findByIdAndItemOwnerId() {
        long bookingId = 2L;
        long itemOwnerId = 1L;

        Optional<Booking> optionalBooking = bookingRepository.findByIdAndItemOwnerId(bookingId, itemOwnerId);
        AssertionsForClassTypes.assertThat(optionalBooking)
                .isPresent()
                .hasValueSatisfying(booking -> AssertionsForClassTypes.assertThat(booking)
                        .hasFieldOrPropertyWithValue("id", 2L))
                .hasValueSatisfying(booking -> AssertionsForClassTypes.assertThat(booking)
                        .hasFieldOrPropertyWithValue("start", booking2.getStart()))
                .hasValueSatisfying(booking -> AssertionsForClassTypes.assertThat(booking)
                        .hasFieldOrPropertyWithValue("end", booking2.getEnd()));
    }

    @Test
    public void findByBooker_IdAndStatusOrderByStartDesc() {
        booking1.setStatus(BookingStatus.APPROVED);
        em.persist(booking1);

        Iterable<Booking> bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(booking1.getBooker().getId(),
                BookingStatus.APPROVED, PageRequest.ofSize(10));

        assertNotNull(bookings);
        assertThat(bookings, iterableWithSize(equalTo(1)));
        booking1.setId(1);
        booking2.setId(2);

        assertThat(bookings, contains(booking1));

    }

    @Test
   public void findByBooker_IdAndEndIsBeforeOrderByStartDesc() {
        Iterable<Booking> bookings = bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(booking1.getBooker().getId(),
                LocalDateTime.now(), PageRequest.ofSize(10));
        assertThat(bookings, iterableWithSize(equalTo(1)));
        booking1.setId(1);
        booking2.setId(2);

        assertThat(bookings, contains(booking1));
    }

    @Test
    public void findByBooker_IdAndStartIsAfterOrderByStartDesc() {
        Iterable<Booking> bookings = bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(booking1.getBooker().getId(),
                LocalDateTime.now(), PageRequest.ofSize(10));

        assertNotNull(bookings);
        assertThat(bookings, iterableWithSize(equalTo(1)));
        booking1.setId(1);
        booking2.setId(2);

        assertThat(bookings, containsInRelativeOrder(booking2));
    }

    @Test
    public void findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        Iterable<Booking> bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(booking1.getBooker().getId(),
                LocalDateTime.now(), LocalDateTime.now(), PageRequest.ofSize(10));
        assertThat(bookings, iterableWithSize(equalTo(0)));
    }

    @Test
    public void findFirstBookingByItem_IdAndStatusNotAndStartAfterOrderByStartAsc() { //Next Booking
        Optional<Booking> optionalBooking = bookingRepository
                .findFirstBookingByItem_IdAndStatusNotAndStartAfterOrderByStartAsc(booking1.getItem().getId(),
                BookingStatus.REJECTED, LocalDateTime.now());

        AssertionsForClassTypes.assertThat(optionalBooking)
                .isPresent()
                .hasValueSatisfying(booking -> AssertionsForClassTypes.assertThat(booking)
                        .hasFieldOrPropertyWithValue("id", 2L));
    }

    @Test
    public void findFirstBookingByItem_IdAndStatusNotAndStartBeforeOrderByStartDesc() { //Last Booking
        Optional<Booking> optionalBooking = bookingRepository
                .findFirstBookingByItem_IdAndStatusNotAndStartBeforeOrderByStartDesc(booking1.getItem().getId(),
                        BookingStatus.REJECTED, LocalDateTime.now());

        AssertionsForClassTypes.assertThat(optionalBooking)
                .isPresent()
                .hasValueSatisfying(booking -> AssertionsForClassTypes.assertThat(booking)
                        .hasFieldOrPropertyWithValue("id", 1L));
    }

      @Test
    public void findByOwner_IdOrderByStartDesc() {
        long itemOwnerId = 1L;

        Iterable<Booking> bookings = bookingRepository.findByOwner_IdOrderByStartDesc(itemOwnerId,
                  PageRequest.ofSize(10));
        assertNotNull(bookings);
        assertThat(bookings, iterableWithSize(equalTo(2)));
        booking1.setId(1);
        booking2.setId(2);

        assertThat(bookings, containsInRelativeOrder(booking2, booking1));
    }

    @Test
    public void findByOwner_IdAndStateCurrent() {
        long itemOwnerId = 1L;

        Iterable<Booking> bookings = bookingRepository.findByOwner_IdAndStateCurrent(itemOwnerId,
                LocalDateTime.now(), LocalDateTime.now(), PageRequest.ofSize(10));
        assertThat(bookings, iterableWithSize(equalTo(0)));
    }

    @Test
    public void findByOwner_IdAndStatePast() {
        long itemOwnerId = 1L;
        Iterable<Booking> bookings = bookingRepository.findByOwner_IdAndStatePast(itemOwnerId,
                LocalDateTime.now(), PageRequest.ofSize(10));
        assertThat(bookings, iterableWithSize(equalTo(1)));
        booking1.setId(1);
        booking2.setId(2);

        assertThat(bookings, contains(booking1));
    }

    @Test
    public void findByOwner_IdAndStateFuture() {
        long itemOwnerId = 1L;
        Iterable<Booking> bookings = bookingRepository.findByOwner_IdAndStateFuture(itemOwnerId,
                LocalDateTime.now(), PageRequest.ofSize(10));

        assertNotNull(bookings);
        assertThat(bookings, iterableWithSize(equalTo(1)));
        booking2.setId(2);

        assertThat(bookings, contains(booking2));
    }

    @Test
    public void findByOwner_IdAndStatus() {
        long itemOwnerId = 1L;
        booking2.setStatus(BookingStatus.REJECTED);
        em.persist(booking2);

        Iterable<Booking> bookings = bookingRepository.findByOwner_IdAndStatus(itemOwnerId, BookingStatus.REJECTED,
                PageRequest.ofSize(10));

        assertThat(bookings, iterableWithSize(equalTo(1)));
        booking1.setId(1);
        booking2.setId(2);

        assertThat(bookings, contains(booking2));
    }

    private Booking getTestBooking1() { //past
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.of(2023, 10, 15, 9, 0));
        booking.setEnd(LocalDateTime.of(2023, 10, 15, 10, 0));
        booking.setItem(new Item(1L, "Test item", "Test item description", true,
                new User(1L, "Test User", "test@mail.ru"), null));
        booking.setBooker(new User(2L, "Test User", "test@mail.ru"));
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    private Booking getTestBooking2() { //future
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.of(2023, 11, 26, 9, 0));
        booking.setEnd(LocalDateTime.of(2023, 11, 26, 10, 0));
        booking.setItem(new Item(1L, "Test item", "Test item description", true, new User(1L, "Test User",
                "test@mail.ru"), null));
        booking.setBooker(new User(2L, "Test User", "test@mail.ru"));
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    private Item getTestItem() {
        Item item = new Item();
        item.setName("Test item");
        item.setDescription("Test item description");
        item.setAvailable(true);
        item.setOwner(new User(1L, "Test User", "test@mail.ru"));

        return item;
    }

    private User getTestOwner() {
        User user = new User();
        user.setName("Test user");
        user.setEmail("test@mail.ru");
        return user;
    }

    private User getTestBooker1() {
        User user = new User();
        user.setName("Test user2");
        user.setEmail("test2@mail.ru");
        return user;
    }

    private User getTestBooker2() {
        User user = new User();
        user.setName("Test user3");
        user.setEmail("test3@mail.ru");
        return user;
    }
}

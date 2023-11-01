package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.RequestBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceItTest {

    private final EntityManager em;
    private final BookingService service;

    @Test
    public void postBooking() {
        User user1 = getTestOwner();
        User user2 = getTestBooker();
        Item item = getTestItem();

        em.createNativeQuery("insert into users (name, email) values (?, ?)")
                .setParameter(1, user1.getName())
                .setParameter(2, user1.getEmail())
                .executeUpdate();

        em.createNativeQuery("insert into users (name, email) values (?, ?)")
                .setParameter(1, user2.getName())
                .setParameter(2, user2.getEmail())
                .executeUpdate();

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User owner = query
                .setParameter("email", user1.getEmail())
                .getSingleResult();

        em.createNativeQuery("insert into items (name, description, available, owner_id) values (?, ?, ?,?)")
                .setParameter(1, item.getName())
                .setParameter(2, item.getDescription())
                .setParameter(3, item.getAvailable())
                .setParameter(4, owner.getId())
                .executeUpdate();

        TypedQuery<User> query1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User booker = query1
                .setParameter("email", user2.getEmail())
                .getSingleResult();

        TypedQuery<Item> query2 = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemOut = query2
                .setParameter("name", ItemMapper.toItemDto(item).getName())
                .getSingleResult();

        RequestBooking requestBooking = new RequestBooking(LocalDateTime.of(2023, 10, 25, 9, 0),
                LocalDateTime.of(2023, 10, 25, 10, 0), itemOut.getId());

        service.postBooking(booker.getId(), requestBooking);
        TypedQuery<Booking> query3 =
                em.createQuery("Select b from Booking b where b.booker.id = :booker_id", Booking.class);
        Booking booking = query3.setParameter("booker_id", booker.getId()).getResultList().get(0);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(requestBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(requestBooking.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(requestBooking.getItemId()));
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

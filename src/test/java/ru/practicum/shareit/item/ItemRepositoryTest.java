package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    ItemRepository itemRepository;

    @Test
    void findItemsByOwnerId() {
        Item newItem1 = getTestItem();
        Item newItem2 = getTestItem2();

        em.persist(getTestUser1());
        em.persist(getTestUser2());
        em.persist(newItem1);
        em.persist(newItem2);

        Iterable<Item> items = itemRepository.findItemByOwnerId(newItem2.getOwner().getId(), PageRequest.ofSize(10));
        assertNotNull(items);
        assertThat(items, iterableWithSize(equalTo(1)));
        assertThat(items, contains(newItem2));
    }

    @Test
    public void search() {
        Item newItem1 = getTestItem();
        Item newItem2 = getTestItem2();

        em.persist(getTestUser1());
        em.persist(getTestUser2());
        em.persist(newItem1);
        em.persist(newItem2);

        Iterable<Item> items = itemRepository.search("TEST", PageRequest.ofSize(10));
        assertNotNull(items);
        assertThat(items, iterableWithSize(equalTo(2)));
        assertThat(items, hasItem(newItem1));
        assertThat(items, hasItem(newItem2));
    }

    private Item getTestItem() {
        Item item = new Item();
        item.setName("Test item");
        item.setDescription("Test item description");
        item.setAvailable(true);
        item.setOwner(new User(1L, "Test User", "test@mail.ru"));

        return item;
    }

    private Item getTestItem2() {
        Item item = new Item();
        item.setName("Test item2");
        item.setDescription("Test item2 description");
        item.setAvailable(true);
        item.setOwner(new User(2L, "Test user2", "test2@mail.ru"));

        return item;
    }

    private User getTestUser1() {
        User user = new User();
        user.setName("Test user");
        user.setEmail("test@mail.ru");
        return user;
    }

    private User getTestUser2() {
        User user = new User();
        user.setName("Test user2");
        user.setEmail("test2@mail.ru");
        return user;
    }

    private Comment getTestComment() {
        Comment comment = new Comment();
        comment.setItem(getTestItem());
        comment.setText("Test comment");
        comment.setAuthor(getTestUser2());
        comment.setCreated(LocalDateTime.of(2023, 10,25, 9,0));
        return comment;
    }

    private Booking getTestBooking() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.of(2023, 10, 25, 9, 0));
        booking.setEnd(LocalDateTime.of(2023, 10, 25, 10, 0));
        booking.setItem(getTestItem());
        booking.setBooker(getTestUser2());
        booking.setStatus(BookingStatus.APPROVED);

        return booking;
    }
}

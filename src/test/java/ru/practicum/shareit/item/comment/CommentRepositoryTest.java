package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void findAllByItemIdIn() {
        em.persist(getTestUser1());
        em.persist(getTestUser2());
        em.persist(getTestItem());
        em.persist(getTestComment());

        Comment expectedComment = getTestComment();
        expectedComment.setId(1L);

        List<Comment> comments = commentRepository.findAllByItemIdIn(Set.of(1L));
        assertThat(comments, hasSize(1));
        assertThat(comments, contains(expectedComment));
    }

    @Test
    public void findAllByItemId() {
        em.persist(getTestUser1());
        em.persist(getTestUser2());
        em.persist(getTestItem());
        em.persist(getTestComment());

        Comment expectedComment = getTestComment();
        expectedComment.setId(1L);

        List<Comment> comments = commentRepository.findAllByItemId(1L);
        assertThat(comments, hasSize(1));
        assertThat(comments, contains(expectedComment));
    }

    private Item getTestItem() {
        Item item = new Item();
        item.setName("Test item");
        item.setDescription("Test item description");
        item.setAvailable(true);
        item.setOwner(new User(1L, "Test User", "test@mail.ru"));

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
        comment.setItem(new Item(1L, "Test item", "Test item description",
                true, new User(1L, "Test User", "test@mail.ru"), null));
        comment.setText("Test comment");
        comment.setAuthor(new User(2L, "Test user2", "test2@mail.ru"));
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

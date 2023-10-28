package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.iterableWithSize;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private User requester1;
    private User requester2;


    @BeforeEach
    public void beforeEach() {
        requester1 = getTestRequester1();
        requester2 = getTestRequester2();
        itemRequest1 = getTestItemRequest1();
        itemRequest2 = getTestItemRequest2();

        em.persist(requester1);
        em.persist(requester2);
        em.persist(itemRequest1);
        em.persist(itemRequest2);
    }

    @Test
    public void findAllByRequestor_IdNot() {
        Iterable<ItemRequest> requests = itemRequestRepository.findAllByRequestor_IdNot(requester1.getId(),
                PageRequest.ofSize(10));
        assertThat(requests, iterableWithSize(1));
        assertThat(requests, contains(itemRequest2));
    }

    @Test
    public void findByRequestor_Id() {
        Iterable<ItemRequest> requests = itemRequestRepository.findByRequestor_Id(requester2.getId());
        assertThat(requests, iterableWithSize(1));
        assertThat(requests, contains(itemRequest2));
    }

    private User getTestRequester1() {
        User user = new User();
        user.setName("Test user2");
        user.setEmail("test2@mail.ru");
        return user;
    }

    private User getTestRequester2() {
        User user = new User();
        user.setName("Test user3");
        user.setEmail("test3@mail.ru");
        return user;
    }

    private ItemRequest getTestItemRequest1() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("Test request");
        itemRequest1.setRequestor(new User(1L, "Test user2", "test2@mail.ru"));
        return itemRequest1;
    }

    private ItemRequest getTestItemRequest2() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("Test request");
        itemRequest1.setRequestor(new User(2L, "Test user2", "test2@mail.ru"));
        return itemRequest1;
    }
}

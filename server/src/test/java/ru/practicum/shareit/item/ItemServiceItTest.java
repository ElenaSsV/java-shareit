package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceItTest {

    private final EntityManager em;
    private final ItemService service;

    @Test
    public void getAllItemsByOwner() {

        User user = getTestUser1();

        em.createNativeQuery("insert into users (name, email) values (?, ?)")
                .setParameter(1, user.getName())
                .setParameter(2, user.getEmail())
                .executeUpdate();

        TypedQuery<User> query1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User owner = query1
                .setParameter("email", user.getEmail())
                .getSingleResult();

        Item item1 = getTestItem1();
        Item item2 = getTestItem2();


        em.createNativeQuery("insert into items (name, description, available, owner_id) values (?, ?, ?,?)")
                .setParameter(1, item1.getName())
                .setParameter(2, item1.getDescription())
                .setParameter(3, item1.getAvailable())
                .setParameter(4, owner.getId())
                .executeUpdate();

        em.createNativeQuery("insert into items (name, description, available, owner_id) values (?, ?, ?,?)")
                .setParameter(1, item2.getName())
                .setParameter(2, item2.getDescription())
                .setParameter(3, item2.getAvailable())
                .setParameter(4, owner.getId())
                .executeUpdate();

        TypedQuery<Item> query2 = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemOut1 = query2
                .setParameter("name", ItemMapper.toItemDto(item1).getName())
                .getSingleResult();

        TypedQuery<Item> query3 = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemOut2 = query3
                .setParameter("name", ItemMapper.toItemDto(item2).getName())
                .getSingleResult();

        ItemWithBookingsAndComments itemResponseDto1 = ItemMapper.toItemWithBookingsAndCommentsDto(itemOut1, Optional.empty(),
                Optional.empty(), new ArrayList<>());
        ItemWithBookingsAndComments itemResponseDto2 = ItemMapper.toItemWithBookingsAndCommentsDto(itemOut2, Optional.empty(),
                Optional.empty(), new ArrayList<>());
        assertThat(service.getAllItemsOwner(owner.getId(), 0, 10),
                equalTo(List.of(itemResponseDto1, itemResponseDto2)));
    }

    private Item getTestItem1() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("Test item description");
        item.setAvailable(true);
        item.setOwner(getTestUser1());
        return item;
    }

    private Item getTestItem2() {
        Item item = new Item();
        item.setId(2L);
        item.setName("Test item2");
        item.setDescription("Test item2 description");
        item.setAvailable(true);
        item.setOwner(getTestUser1());
        return item;
    }

    private User getTestUser1() {
        User user = new User();
        user.setId(1L);
        user.setName("Test user");
        user.setEmail("test@mail.ru");
        return user;
    }

}

package ru.practicum.shareit;


import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemControllerTest {

    private final ItemService itemService;
    private final UserService userService;

    private UserDto getTestUser1() {
        UserDto user1 = new UserDto();
        user1.setName("testUser1");
        user1.setEmail("test@test.ru");
        return user1;
    }

    private ItemDto getTestItem1() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test item");
        itemDto.setDescription("Test description");
        itemDto.setAvailable(true);
        return itemDto;
    }

    private ItemDto getTestItem2() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Searched tool");
        itemDto.setDescription("Test description");
        itemDto.setAvailable(true);
        return itemDto;
    }

    @Test
    public void postItemTest() {
        UserDto user = userService.postUser(getTestUser1());

        Optional<ItemDto> optionalItem = Optional.of(itemService.postItem(user.getId(), getTestItem1()));

        assertThat(optionalItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("name", "Test item"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("description", "Test description"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("available", true));
    }

    @Test
    public void partiallyUpdateItem() throws JsonMappingException {
        UserDto user = userService.postUser(getTestUser1());
        ItemDto savedItem = itemService.postItem(user.getId(), getTestItem1());

        ItemDto toUpdateName = new ItemDto();
        toUpdateName.setName("Update name");
        itemService.partiallyUpdateItem(user.getId(), savedItem.getId(), toUpdateName);

        Optional<ItemDto> updatedItem = Optional.of(itemService.getItemByIdAnyUser(savedItem.getId()));

        assertThat(updatedItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("name", "Update name"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("description", "Test description"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("available", true));

        ItemDto toUpdateAvailable = new ItemDto();
        toUpdateAvailable.setAvailable(false);
        itemService.partiallyUpdateItem(user.getId(), savedItem.getId(), toUpdateAvailable);

        Optional<ItemDto> updatedItem2 = Optional.of(itemService.getItemByIdAnyUser(savedItem.getId()));

        assertThat(updatedItem2)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("name", "Update name"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("description", "Test description"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("available", false));
    }

    @Test
    public void getAllItemsTest() {
        UserDto user = userService.postUser(getTestUser1());

        List<ItemDto> emptyItemList = itemService.getAllItemsOwner(user.getId());
        assertTrue(emptyItemList.isEmpty());

        ItemDto savedItem = itemService.postItem(user.getId(), getTestItem1());

        List<ItemDto> items = itemService.getAllItemsOwner(user.getId());
        assertEquals(1, items.size());

        Optional<ItemDto> optionalItem = Optional.of(items.get(0));

        assertThat(optionalItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("name", "Test item"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("description", "Test description"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("available", true));
    }

    @Test
    public void searchTest() {
        UserDto user = userService.postUser(getTestUser1());
        ItemDto savedItem1 = itemService.postItem(user.getId(), getTestItem1());
        ItemDto savedItem2 = itemService.postItem(user.getId(), getTestItem2());

        List<ItemDto> searchedItems2 = itemService.searchItem(user.getId(), "test");
        assertEquals(2, searchedItems2.size());

        Optional<ItemDto> optionalItem1 = Optional.of(searchedItems2.get(0));

        assertThat(optionalItem1)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("name", "Test item"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("description", "Test description"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("available", true));

        Optional<ItemDto> optionalItem2 = Optional.of(searchedItems2.get(1));

        assertThat(optionalItem2)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("id", 2L))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("name", "Searched tool"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("description", "Test description"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("available", true));

        List<ItemDto> searchedItems1 = itemService.searchItem(user.getId(), "tOOL");
        assertEquals(1, searchedItems1.size());

        Optional<ItemDto> optionalItem = Optional.of(searchedItems1.get(0));

        assertThat(optionalItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("id", 2L))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("name", "Searched tool"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("description", "Test description"))
                .hasValueSatisfying(item -> assertThat(item).hasFieldOrPropertyWithValue("available", true));
    }





}

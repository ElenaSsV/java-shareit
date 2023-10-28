package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseWithItems;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestWithItemsTest {

    @Autowired
    private JacksonTester<ItemRequestResponseWithItems> json;

    @Test
    void testItemRequestResponse() throws IOException {

        ItemDto item = getTestItem();
        List<ItemDto> items = Collections.singletonList(item);

        ItemRequestResponseWithItems itemRequestResponse = getTestItemRequest();

        var res = json.write(itemRequestResponse);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.created");
        assertThat(res).hasJsonPath("$.items");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestResponse.getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestResponse.getDescription());
        assertThat(res).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestResponse.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(res).extractingJsonPathArrayValue("$.items").isInstanceOf(ArrayList.class);
        assertThat(res).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(itemRequestResponse.getItems().get(0).getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(itemRequestResponse.getItems().get(0).getName());
        assertThat(res).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(itemRequestResponse.getItems().get(0).getDescription());
    }

    private ItemDto getTestItem() {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("Test item description");
        item.setAvailable(true);
        return item;
    }

    private ItemRequestResponseWithItems getTestItemRequest() {
        ItemRequestResponseWithItems itemRequest1 = new ItemRequestResponseWithItems();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Test request");
        itemRequest1.setCreated(LocalDateTime.of(2023, 10, 15, 9, 0));
        itemRequest1.setItems(List.of(new ItemDto(1L, "Test item", "Test item description",
                true, null)));
        return itemRequest1;
    }
}

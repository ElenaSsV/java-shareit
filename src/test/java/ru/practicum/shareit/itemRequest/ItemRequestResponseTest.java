package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestResponseTest {

    @Autowired
    private JacksonTester<ItemRequestResponse> json;

    @Test
    void testItemRequestResponse() throws IOException {

        ItemDto item = getTestItem();
        List<ItemDto> items = Collections.singletonList(item);

        ItemRequestResponse itemRequestResponse = getTestItemRequest();

        var res = json.write(itemRequestResponse);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.created");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestResponse.getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestResponse.getDescription());
        assertThat(res).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestResponse.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }

    private ItemDto getTestItem() {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("Test item description");
        item.setAvailable(true);
        return item;
    }

    private ItemRequestResponse getTestItemRequest() {
        ItemRequestResponse itemRequest1 = new ItemRequestResponse();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Test request");
        itemRequest1.setCreated(LocalDateTime.of(2023, 10, 15, 9, 0));
        return itemRequest1;
    }

}

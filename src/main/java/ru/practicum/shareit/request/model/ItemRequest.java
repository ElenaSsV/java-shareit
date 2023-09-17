package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private long id;
    @NotBlank
    private String description;
    private final User requestor;
    private final LocalDateTime created = LocalDateTime.now();
}

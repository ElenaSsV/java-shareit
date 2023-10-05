package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private Instant created = Instant.now();
}

package ru.practicum.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
   // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddHH:mm:ss")
    private LocalDateTime created = LocalDateTime.now();
}

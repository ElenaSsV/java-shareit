package ru.practicum.shareit.item.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.List;
import java.util.Set;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemIdIn(Set<Long> itemsIds);

    List<Comment> findAllByItemId(long itemId);
}

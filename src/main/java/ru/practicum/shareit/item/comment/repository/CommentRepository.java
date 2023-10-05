package ru.practicum.shareit.item.comment.repository;
import ru.practicum.shareit.item.comment.model.Comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByItemIdIn(Set<Long> itemsIds);
}

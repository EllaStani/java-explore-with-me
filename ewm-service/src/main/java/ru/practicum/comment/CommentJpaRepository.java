package ru.practicum.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findCommentByEventId(Integer eventId);

    List<Comment> findCommentByEventIdIn(List<Integer> events);
}


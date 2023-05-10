package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentNewDto;
import ru.practicum.common.FromSizeRequest;
import ru.practicum.common.State;
import ru.practicum.event.Event;
import ru.practicum.event.EventJpaRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.user.User;
import ru.practicum.user.UserJpaRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentJpaRepository commentRepository;
    private final UserJpaRepository userRepository;
    private final EventJpaRepository eventRepository;

    @Override
    public CommentDto getCommentById(int commentId) {
        Comment comment = checkingExistComment(commentId);
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByEventId(int eventId, int from, int size) {
        checkingExistEvent(eventId);
        Pageable pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        List<Comment> comments = commentRepository.findCommentByEventId(eventId, pageable);
        return CommentMapper.mapToListCommentDto(comments);
    }

    @Override
    public CommentDto saveNewComment(int userId, int eventId, CommentNewDto commentNewDto) {
        User user = checkingExistUser(userId);
        Event event = checkingExistEvent(eventId);
        if (event.getState() != State.PUBLISHED) {
            log.error("Нельзя оставить комментарий к неопубликованному событию.");
            throw new ConflictException("Нельзя оставить комментарий к неопубликованному событию.");
        }
        Comment comment = commentRepository.save(CommentMapper.mapToComment(user, event, commentNewDto));
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public CommentDto updatePrivateComment(int userId, int commentId, CommentNewDto commentNewDto) {
        Comment comment = checkingExistComment(commentId);
        if (comment.getUser().getId() != userId) {
            log.error("Изменить комментарий может только его автор.");
            throw new ConflictException("Изменить комментарий может только его автор.");
        }
        comment.setText(commentNewDto.getText());
        Comment newComment = commentRepository.save(comment);
        return CommentMapper.mapToCommentDto(newComment);
    }

    @Override
    public void deletePrivateComment(int userId, int commentId) {
        Comment comment = checkingExistComment(commentId);
        if (comment.getUser().getId() != userId) {
            log.error("Удалить комментарий может только его автор.");
            throw new ConflictException("Удалить комментарий может только его автор.");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteAdminComment(int commentId) {
        checkingExistComment(commentId);
        commentRepository.deleteById(commentId);
    }

    private User checkingExistUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ConflictException(String.format("Пользователь с id=%s не найден", userId)));
    }

    private Event checkingExistEvent(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ConflictException(String.format("Событие с id=%s не найдено", eventId)));
    }

    private Comment checkingExistComment(int commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ConflictException(String.format("Комментарий с id=%s не найден", commentId)));
    }
}

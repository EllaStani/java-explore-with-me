package ru.practicum.comment;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentNewDto;

import java.util.List;

public interface CommentService {
    CommentDto getCommentById(int commentId);

    List<CommentDto> getCommentsByEventId(int eventId, int from, int size);

    CommentDto saveNewComment(int userId, int eventId, CommentNewDto commentNewDto);

    CommentDto updatePrivateComment(int userId, int commentId, CommentNewDto commentNewDto);

    void deletePrivateComment(int userId, int commentId);

    void deleteAdminComment(int commentId);
}

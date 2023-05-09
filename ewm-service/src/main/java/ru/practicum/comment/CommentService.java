package ru.practicum.comment;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentNewDto;

public interface CommentService {
    CommentDto saveNewComment(int userId, int eventId, CommentNewDto commentNewDto);

    CommentDto updatePrivateComment(int userId, int commentId, CommentNewDto commentNewDto);

    void deletePrivateComment(int userId, int commentId);

    void deleteAdminComment(int commentId);
}

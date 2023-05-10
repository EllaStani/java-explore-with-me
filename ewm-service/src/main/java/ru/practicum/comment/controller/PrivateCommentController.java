package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentNewDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class PrivateCommentController {
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/users/{userId}/events/{eventId}")
    public CommentDto saveNewComment(@PathVariable(value = "userId") int userId,
                                     @PathVariable(value = "eventId") int eventId,
                                     @RequestBody @Valid CommentNewDto commentNewDto) {
        log.info("API PrivateComment. POST параметры: userId={}, eventId={}", userId, eventId);
        CommentDto commentDto = commentService.saveNewComment(userId, eventId, commentNewDto);
        log.info("API PrivateComment. POST: Добавлен комментарий: {}", commentDto);
        return commentDto;

    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/{commentId}/users/{userId}")
    public CommentDto updateComment(@PathVariable(value = "userId") int userId,
                                    @PathVariable(value = "commentId") int commentId,
                                    @RequestBody @Valid CommentNewDto commentNewDto) {
        log.info("API PrivateComment. PATCH параметры: userId={}, commentId={}, commentNewDto={}",
                userId, commentId, commentNewDto);
        CommentDto commentDto = commentService.updatePrivateComment(userId, commentId, commentNewDto);
        log.info("API PrivateEvent. PATCH: Комментарий изменен: {}", commentDto);
        return commentDto;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}/users/{userId}")
    public void deleteComment(@PathVariable(value = "userId") int userId,
                              @PathVariable(value = "commentId") int commentId) {
        commentService.deletePrivateComment(userId, commentId);
        log.info("API PrivateComment. DELETE: Пользователь удалил комментарий userId={}, commentId={}", userId, commentId);
    }
}

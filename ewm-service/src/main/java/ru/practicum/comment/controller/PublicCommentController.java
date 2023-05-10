package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
@Validated
public class PublicCommentController {
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CommentDto> getComments(
            @RequestParam(value = "eventId", required = false) int eventId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {

        log.info("API PublicComment. GET: параметры запроса eventId={}, from = {}, size = {}", eventId, from, size);
        List<CommentDto> commentDtos = commentService.getCommentsByEventId(eventId, from, size);
        log.info("API PublicComment. GET: найдено событий={}", commentDtos.size());
        return commentDtos;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable Integer commentId) {
        log.info("API PublicComment. GET: параметры поиска: commentId={}", commentId);
        CommentDto commentDto = commentService.getCommentById(commentId);
        log.info("API PublicComment. GET:  найден комментарий: {}", commentDto);
        return commentDto;
    }
}

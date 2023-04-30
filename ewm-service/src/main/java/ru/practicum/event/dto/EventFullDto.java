package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.category.CategoryDto;
import ru.practicum.common.State;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventFullDto {
    private int id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private LocationDto locationDto;
    private boolean paid;
    private Integer participantLimit;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private State state;
    private String title;
    private Long views;

    @Override
    public String toString() {
        return "\n" + "EventFullDto = {" +
                "id=" + id +  ", \n"  +
                "annotation='" + annotation + ", \n"  +
                "category='" + category + ", \n"  +
                "title='" + title + ", \n"  +
                "confirmedRequests=" + confirmedRequests + ", \n"  +
                "eventDate='" + eventDate + ", \n"  +
                "createdOn=" + createdOn + ", \n"  +
                "publishedOn=" + publishedOn + ", \n" +
                "description='" + description + ", \n" +
                "initiator='" + initiator + ", \n" +
                "locationDto='" + locationDto + ", \n" +
                "paid='" + paid + ", \n"  +
                "requestModeration='" + requestModeration + ", \n"  +
                "participantLimit='" + participantLimit + ", \n" +
                "state='" + state + ", \n"  +
                "views='" + views + ", \n"  +
                '}';
    }
}


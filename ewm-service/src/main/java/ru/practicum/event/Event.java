package ru.practicum.event;

import lombok.*;
import ru.practicum.category.Category;
import ru.practicum.common.State;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "annotation")
    private String annotation;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Column(name = "lat")
    private float lat;
    @Column(name = "lon")
    private float lon;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;
}

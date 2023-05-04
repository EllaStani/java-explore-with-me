package ru.practicum.request;

import lombok.*;
import ru.practicum.common.Status;
import ru.practicum.event.Event;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    public int getIdEvent() {
        return this.event.getId();
    }

    @Override
    public String toString() {
        return "\n" + "Request = {" +
                "   id=" + id + ", " +
                "   status'" + status + ", " +
                "   requester_id='" + requester.getId() + ", " +
                "   event_id='" + event.getId();
    }
}

package ru.practicum.compilation;

import lombok.*;
import ru.practicum.event.Event;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "pinned")
    private boolean pinned;

    @Column(name = "title")
    private String title;
    @ManyToMany (cascade = { CascadeType.ALL })
    @JoinTable(
            name = "events_compilations",
            joinColumns = {@JoinColumn(name = "compilations_id")},
            inverseJoinColumns = {@JoinColumn(name = "events_id")}
    )
    private List<Event> events;

}

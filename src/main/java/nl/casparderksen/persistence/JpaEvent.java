package nl.casparderksen.persistence;

import lombok.*;
import nl.casparderksen.model.Event;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
@Table(name = "EVENT")
@NamedQueries({
        @NamedQuery(name = "JpaEvent.findAll", query = "SELECT e FROM JpaEvent e"),
        @NamedQuery(name = "JpaEvent.countAll", query = "SELECT count(e) FROM JpaEvent e")
})
class JpaEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IdSequence")
    @SequenceGenerator(name = "IdSequence", sequenceName = "ID_SEQ", allocationSize = 1)
    private long id;

    @NotNull
    @NonNull
    private String name;

    public JpaEvent merge(Event event) {
        name = event.getName();
        return this;
    }

    public Event toEvent() {
        return new Event(id, name);
    }
}
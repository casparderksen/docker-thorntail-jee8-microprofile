package nl.casparderksen.persistence;

import lombok.*;
import nl.casparderksen.model.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
@Table(name = "DOCUMENT")
@NamedQueries({
        @NamedQuery(name = "JpaDocument.findAll", query = "SELECT e FROM JpaDocument e"),
        @NamedQuery(name = "JpaDocument.countAll", query = "SELECT count(e) FROM JpaDocument e")
})
class JpaDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IdSequence")
    @SequenceGenerator(name = "IdSequence", sequenceName = "ID_SEQ", allocationSize = 1)
    private long id;

    @NotNull
    @NonNull
    private String name;

    public JpaDocument merge(Document document) {
        name = document.getName();
        return this;
    }

    public Document toDocument() {
        return new Document(id, name);
    }
}
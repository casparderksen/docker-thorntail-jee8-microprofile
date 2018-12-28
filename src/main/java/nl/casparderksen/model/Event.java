package nl.casparderksen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class Event {

    private long id;
    private String name;

    public Event(String name) {
        this.name = name;
    }
}

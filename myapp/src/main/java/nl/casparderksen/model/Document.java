package nl.casparderksen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class Document {

    private long id;
    private String name;

    public Document(String name) {
        this.name = name;
    }
}

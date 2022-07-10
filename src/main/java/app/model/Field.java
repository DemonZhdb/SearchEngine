package app.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "field", schema = "search_engine")
public class Field {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "selector")
    private String selector;

    @Column(name = "weight")
    private float weight;

    public Field(int rsInt, String rsString) {

    }

    public Field() {

    }
}

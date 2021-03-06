package app.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@Table(name = "lemma" )
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "lemma")
    private String lemma;

    private int frequency;

    @ManyToOne()
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    private Site siteByLemma;

    @OneToMany(mappedBy = "lemmaByIndex")
    private Collection<Index> indexesById;


}

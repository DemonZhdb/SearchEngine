package app.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "indexPage", schema = "search_engine")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = true)
    private int id;

    @ManyToOne
    @JoinColumn(name = "page_id", referencedColumnName = "id", nullable = false)
    private Page pageByIndex;

    @ManyToOne
    @JoinColumn(name = "lemma_id", referencedColumnName = "id", nullable = false)
    private Lemma lemmaByIndex;


    @Column(name = "rank_lemma")
    private float rankLemma;


}

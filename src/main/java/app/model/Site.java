package app.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;


@Entity
@Data
@Table(name = "site", schema = "search_engine")
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "enum('INDEXING', 'INDEXED', 'FAILED')")
    private StatusIndexing status;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;

    @Column(name = "url")
    private String url;

    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "siteByLemma")
    private Collection<Lemma> lemmasById;

    @OneToMany(mappedBy = "siteByPage")
    private Collection<Page> pagesById;


}

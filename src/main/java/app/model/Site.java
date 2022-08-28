package app.model;


import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;


@Entity
@Data
@Table(name = "site")
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)

public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "status_enum")
    @Type(type = "pgsql_enum")
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

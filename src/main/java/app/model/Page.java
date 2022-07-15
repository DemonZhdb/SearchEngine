package app.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@Table(name = "page" )
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "path", columnDefinition = "TEXT")
    private String path;

    @Column(name = "code")
    private int code;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;

    @ManyToOne()
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    private Site siteByPage;

    @OneToMany(mappedBy = "pageByIndex")
    private Collection<Index> indexesById;


}

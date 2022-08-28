package app.repository;

import app.model.Page;
import app.model.Site;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends CrudRepository<Page, Integer> {

    Page findByPathAndSiteByPage(String path, Site siteByPage);

    Iterable<Page> findBySiteByPage(Site siteByPage);

    long countBySiteByPage(Site siteByPage);

    void deleteBySiteByPage(Site siteByPage);

    @Override
    @Modifying
    @Query("DELETE FROM Page")
    void deleteAll();

}

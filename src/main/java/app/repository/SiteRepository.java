package app.repository;

import app.model.Site;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SiteRepository extends CrudRepository<Site, Integer> {
    Site findByName(String siteName);

    Site findByUrl(String siteUrl);
    List<Site> findAll();

    @Override
    @Modifying
    @Query("DELETE FROM Page")
    void deleteAll();
}

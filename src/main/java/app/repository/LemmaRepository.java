package app.repository;

import app.model.Lemma;
import app.model.Site;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma, Integer> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO lemma(lemma, site_id, frequency ) " + "VALUES(?,?,?) " +
            "ON DUPLICATE KEY UPDATE frequency=frequency + 1", nativeQuery = true)
    void insertLemmaAndSite(String lemma, Site siteByLemma, int frequency);

    Lemma findByLemmaAndSiteByLemma(String lemma, Site siteByLemma);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE lemma  SET frequency = frequency - 1 " +
            "where id in (select lemma_id from index_page where page_id = ?);", nativeQuery = true)
    void updateLemmaFrequency(int pageId);

    Lemma findByIdAndSiteByLemma(int id, Site siteByLemma);

    long countBySiteByLemma(Site siteByLemma);

    List<Lemma> findByLemmaInAndSiteByLemmaOrderByFrequency(List<String> lemmas, Site siteByLemma);

    @Modifying
    @Query("DELETE FROM Lemma l WHERE l.frequency=0")
    void deleteOfFrequencyIsNull();

    @Override
    @Modifying
    @Query("DELETE FROM Lemma")
    void deleteAll();
}

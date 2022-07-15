package app.repository;

import app.DTO.RelevancePage;
import app.model.Index;
import app.model.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexRepository extends PagingAndSortingRepository<Index, Integer> {

    @Query(value = "WITH temp_sel AS (SELECT  page_id as page_id, SUM( rank_lemma) AS sum_rank " +
            "FROM index_page JOIN" +
            " query_lemma  USING (lemma_id) where page_id IN " +
            "(SELECT i.page_id FROM " +
            "query_lemma q JOIN index_page i USING (lemma_id)  " +
            "GROUP BY i.page_id HAVING count(i.page_id) =(SELECT COUNT(*) FROM query_lemma)) " +
            "GROUP BY page_id ) " +
            "SELECT s.url AS site, s.name AS siteName, p.path " +
            "AS uri, p.content AS content, sum_rank/(SELECT MAX(sum_rank) FROM temp_sel) AS relevance " +
            "FROM temp_sel t JOIN page p ON t.page_id = p.id JOIN site s ON p.site_id = s.id ", nativeQuery = true)
    List<RelevancePage> findPagesOfRelevance(Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM (SELECT  page_id as page_id, SUM( rank_lemma) AS sum_rank " +
            "FROM index_page JOIN" +
            " query_lemma USING (lemma_id) where page_id IN " +
            "(SELECT i.page_id FROM " +
            "query_lemma q JOIN index_page i USING (lemma_id)  " +
            "GROUP BY i.page_id HAVING count(i.page_id) =(SELECT COUNT(*) FROM query_lemma)) " +
            "GROUP BY page_id ) query", nativeQuery = true)
    Integer findCountOfPages();

    void deleteByPageByIndex(Page page);

    @Override
    @Modifying
    @Query("DELETE FROM Index")
    void deleteAll();


}
